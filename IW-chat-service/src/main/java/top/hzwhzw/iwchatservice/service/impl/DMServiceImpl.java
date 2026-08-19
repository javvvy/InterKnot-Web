package top.hzwhzw.iwchatservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dto.DMConversationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.hzwhzw.iwapi.client.UserClient;
import top.hzwhzw.iwchatservice.mapper.DMConversationMapper;
import top.hzwhzw.iwchatservice.mapper.DMMessageMapper;
import top.hzwhzw.iwchatservice.pojo.DMConversation;
import top.hzwhzw.iwchatservice.pojo.DMMessage;
import top.hzwhzw.iwchatservice.service.DMService;
import top.hzwhzw.iwchatservice.websocket.DmWebSocketSessionManager;
import utils.UserContextHolder;
import vo.DMConversationVO;
import vo.DMMessageVO;
import vo.UserVO2;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DMServiceImpl implements DMService {
    private final DMConversationMapper dmConversationMapper;
    private final DMMessageMapper dmMessageMapper;
    private final UserClient userClient;
    private final DmWebSocketSessionManager wsSessionManager;

    @Override
    public List<DMConversationVO> getDMConversationList() {
        Long userId = UserContextHolder.getUserId();
        List<DMConversation> dmConversationList = dmConversationMapper.selectList(new LambdaQueryWrapper<DMConversation>()
                .eq(DMConversation::getUserId, userId)
                .orderByDesc(DMConversation::getLastMessageAt)
        );
        return dmConversationList.stream()
                .map(this::toDMConversationVO)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DMConversationVO createDMConversation(String targetUserNo) {
        Long userId = UserContextHolder.getUserId();
        var targetUser = userClient.queryUserByUserNo(targetUserNo);
        if (targetUser == null || targetUser.getId() == null) {
            throw new IllegalArgumentException("目标用户不存在");
        }
        Long targetUserId = targetUser.getId();
        if (targetUserId.equals(userId)) {
            throw new IllegalArgumentException("无法与自己创建私聊会话");
        }
        // 双行模型：查找当前用户这一行即可
        DMConversation mine = dmConversationMapper.selectOne(new LambdaQueryWrapper<DMConversation>()
                .eq(DMConversation::getUserId, userId)
                .eq(DMConversation::getPeerId, targetUserId)
        );
        if (mine != null) {
            return toDMConversationVO(mine);
        }
        // 创建两行，共享同一个 conversationNo
        String conversationNo = "dmCON-" + IdUtil.getSnowflakeNextIdStr();
        mine = buildConversation(conversationNo, userId, targetUserId);
        dmConversationMapper.insert(mine);
        dmConversationMapper.insert(buildConversation(conversationNo, targetUserId, userId));
        return toDMConversationVO(mine);
    }

    private DMConversation buildConversation(String conversationNo, Long userId, Long peerId) {
        DMConversation dmConversation = new DMConversation();
        dmConversation.setConversationNo(conversationNo);
        dmConversation.setUserId(userId);
        dmConversation.setPeerId(peerId);
        dmConversation.setKind("direct");
        dmConversation.setMemberCount(2L);
        dmConversation.setUnreadCount(0L);
        dmConversation.setMuted(false);
        dmConversation.setPinned(false);
        return dmConversation;
    }

    @Override
    public Object getDMConversationMessages(DMConversationDTO dmConversationDTO) {
        int limit = dmConversationDTO.getLimit() != null ? dmConversationDTO.getLimit() : 50;
        if (dmConversationDTO.getBefore() == null) {
            dmConversationDTO.setBefore(LocalDateTime.now());
        }
        IPage<DMMessage> page = new Page<>(1, limit);
        IPage<DMMessage> dmMessagePage = dmMessageMapper.selectPage(page, new LambdaQueryWrapper<DMMessage>()
                .eq(DMMessage::getConversationNo, dmConversationDTO.getConversationNo())
                .lt(DMMessage::getCreatedAt, dmConversationDTO.getBefore())
                .orderByAsc(DMMessage::getCreatedAt)
        );
        List<DMMessageVO> dmMessageVOList = dmMessagePage.getRecords().stream()
                .map(this::toDMMessageVO)
                .toList();
        Page<DMMessageVO> voPage = new Page<>(1, limit);
        voPage.setRecords(dmMessageVOList);
        voPage.setTotal(dmMessagePage.getTotal());
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DMMessageVO sendMessage(String conversationNo, DMConversationDTO dmConversationDTO) {
        Long userId = UserContextHolder.getUserId();
        getConversationOrThrow(conversationNo, userId);

        // 1. 创建并持久化消息
        DMMessage dmMessage = new DMMessage();
        dmMessage.setConversationNo(conversationNo);
        dmMessage.setMessageNo("message-" + IdUtil.getSnowflakeNextIdStr());
        dmMessage.setSenderUserId(userId);
        dmMessage.setKind(dmConversationDTO.getKind() != null ? dmConversationDTO.getKind() : "text");
        dmMessage.setContent(dmConversationDTO.getContent());
        dmMessage.setCreatedAt(LocalDateTime.now());
        dmMessageMapper.insert(dmMessage);

        // 2. 更新两行的最后一条消息
        dmConversationMapper.update(null, new LambdaUpdateWrapper<DMConversation>()
                .eq(DMConversation::getConversationNo, conversationNo)
                .set(DMConversation::getLastMessageNo, dmMessage.getMessageNo())
                .set(DMConversation::getLastMessageAt, dmMessage.getCreatedAt()));

        // 3. 只给对端那一行的未读数 +1
        dmConversationMapper.update(null, new LambdaUpdateWrapper<DMConversation>()
                .eq(DMConversation::getConversationNo, conversationNo)
                .ne(DMConversation::getUserId, userId)
                .setSql("unread_count = unread_count + 1"));

        DMMessageVO dmMessageVO = toDMMessageVO(dmMessage);

        // 4. WebSocket 广播 message.created
        Map<String, Object> event = wsSessionManager.buildEvent("message.created");
        event.put("conversationId", conversationNo);
        event.put("messageId", dmMessage.getMessageNo());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("message", dmMessageVO);
        event.put("data", data);
        wsSessionManager.broadcastToConversation(conversationNo, event, userId.toString());

        return dmMessageVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(String messageNo) {
        // 鉴权：仅发送者可撤回
        DMMessage dmMessage = dmMessageMapper.selectOne(new LambdaQueryWrapper<DMMessage>()
                .eq(DMMessage::getMessageNo, messageNo)
                .eq(DMMessage::getSenderUserId, UserContextHolder.getUserId())
        );
        if (dmMessage == null) {
            throw new IllegalArgumentException("消息不存在或无权撤回");
        }
        dmMessageMapper.delete(new LambdaQueryWrapper<DMMessage>()
                .eq(DMMessage::getMessageNo, messageNo));

        // WebSocket 广播 message.deleted
        Map<String, Object> event = wsSessionManager.buildEvent("message.deleted");
        event.put("conversationId", dmMessage.getConversationNo());
        event.put("messageId", messageNo);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("deletedAt", LocalDateTime.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        event.put("data", data);
        wsSessionManager.broadcastToConversation(dmMessage.getConversationNo(), event, UserContextHolder.getUserId().toString());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markConversationRead(String conversationNo) {
        Long userId = UserContextHolder.getUserId();
        getConversationOrThrow(conversationNo, userId);
        dmConversationMapper.update(null, new LambdaUpdateWrapper<DMConversation>()
                .eq(DMConversation::getConversationNo, conversationNo)
                .eq(DMConversation::getUserId, userId)
                .set(DMConversation::getUnreadCount, 0)
        );

        // WebSocket 广播 conversation.read
        Map<String, Object> event = wsSessionManager.buildEvent("conversation.read");
        event.put("conversationId", conversationNo);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("lastReadAt", LocalDateTime.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        event.put("data", data);
        wsSessionManager.broadcastToConversation(conversationNo, event, userId.toString());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDMConversation(String conversationNo, DMConversationDTO dmConversationDTO) {
        Long userId = UserContextHolder.getUserId();
        DMConversation dmConversation = getConversationOrThrow(conversationNo, userId);
        dmConversation.setMuted(dmConversationDTO.getMuted());
        dmConversation.setPinned(dmConversationDTO.getPinned());
        dmConversationMapper.updateById(dmConversation);

        // WebSocket 广播 conversation.updated
        Map<String, Object> event = wsSessionManager.buildEvent("conversation.updated");
        event.put("conversationId", conversationNo);
        Map<String, Object> data = new LinkedHashMap<>();
        if (dmConversationDTO.getMuted() != null) data.put("muted", dmConversationDTO.getMuted());
        if (dmConversationDTO.getPinned() != null) data.put("pinned", dmConversationDTO.getPinned());
        event.put("data", data);
        wsSessionManager.broadcastToConversation(conversationNo, event, userId.toString());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDMConversation(String conversationNo) {
        Long userId = UserContextHolder.getUserId();
        getConversationOrThrow(conversationNo, userId);
        dmConversationMapper.delete(new LambdaQueryWrapper<DMConversation>()
                .eq(DMConversation::getConversationNo, conversationNo)
                .eq(DMConversation::getUserId, userId));

        // WebSocket 广播 conversation.member.removed
        Map<String, Object> event = wsSessionManager.buildEvent("conversation.member.removed");
        event.put("conversationId", conversationNo);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userId", userId);
        event.put("data", data);
        wsSessionManager.broadcastToConversation(conversationNo, event, userId.toString());
    }

    private DMConversation getConversationOrThrow(String conversationNo, Long userId) {
        DMConversation dmConversation = dmConversationMapper.selectOne(new LambdaQueryWrapper<DMConversation>()
                .eq(DMConversation::getConversationNo, conversationNo)
                .eq(DMConversation::getUserId, userId)
        );
        if (dmConversation == null) {
            throw new IllegalArgumentException("会话不存在");
        }
        return dmConversation;
    }

    private DMConversationVO toDMConversationVO(DMConversation dmConversation) {
        DMConversationVO dmConversationVO = BeanUtil.copyProperties(dmConversation, DMConversationVO.class);
        // 双行模型：当前行就是对端，直接取 peerId
        dmConversationVO.setPeer(userClient.queryUserById(dmConversation.getPeerId()));
        // 封装最后一条消息
        if (dmConversation.getLastMessageNo() != null) {
            DMMessage lastMessage = dmMessageMapper.selectOne(new LambdaQueryWrapper<DMMessage>()
                    .eq(DMMessage::getMessageNo, dmConversation.getLastMessageNo())
            );
            if (lastMessage != null) {
                dmConversationVO.setLastMessage(toDMMessageVO(lastMessage));
            }
        }
        return dmConversationVO;
    }

    private DMMessageVO toDMMessageVO(DMMessage dmMessage) {
        DMMessageVO dmMessageVO = BeanUtil.copyProperties(dmMessage, DMMessageVO.class);
        UserVO2 sender = userClient.queryUserById(dmMessage.getSenderUserId());
        dmMessageVO.setIsSelf(dmMessage.getSenderUserId().equals(UserContextHolder.getUserId()));
        dmMessageVO.setSender(sender);
        return dmMessageVO;
    }
}
