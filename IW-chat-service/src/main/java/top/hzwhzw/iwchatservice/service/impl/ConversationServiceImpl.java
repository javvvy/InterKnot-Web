package top.hzwhzw.iwchatservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dto.ConversationDTO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pojo.KnockMeta;
import top.hzwhzw.iwapi.client.ArticleClient;
import top.hzwhzw.iwapi.client.CommentClient;
import top.hzwhzw.iwapi.client.UserClient;
import top.hzwhzw.iwchatservice.mapper.ConversationMapper;
import top.hzwhzw.iwchatservice.mapper.MessageMapper;
import top.hzwhzw.iwchatservice.pojo.Conversation;
import top.hzwhzw.iwchatservice.pojo.Message;
import top.hzwhzw.iwchatservice.service.ConversationService;
import utils.UserContextHolder;
import vo.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements ConversationService {
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;
    private final UserClient userClient;
    private final ArticleClient articleClient;
    private final CommentClient commentClient;

    @Override
    public Object getConversationList() {
        Long userId = UserContextHolder.getUserId();
        List<Conversation> conversations = conversationMapper.selectList(
                new LambdaQueryWrapper<Conversation>()
                        .eq(Conversation::getUserId, userId)
                        .or()
                        .eq(Conversation::getPeerKey, userId));
        List<ConversationVO> conversationVOs = conversations.stream()
                .map(conversation -> BeanUtil.copyProperties(conversation, ConversationVO.class))
                .toList();
        KnockMeta meta = new KnockMeta();
        meta.setTotal((long) conversations.size());
        meta.setTruncated(false);
        meta.setScannedRows(conversations.size());
        meta.setCap(1000);
        return ConversationsVO.builder()
                .conversations(conversationVOs)
                .meta(meta)
                .build();
    }
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public IPage<MessageVO> getConversationMessages(ConversationDTO conversationDTO) {
        int queryLimit = (conversationDTO.getLimit() != null ? conversationDTO.getLimit() : 50);
        if (conversationDTO.getBefore() == null) {
            conversationDTO.setBefore(LocalDateTime.now());
        }
        //1.创建page对象
        Page<Message> page = new Page<Message>(1, queryLimit);
        // 2. 调用分页查询方法，传入 Page 对象
        IPage<Message> messagePage = messageMapper.selectPage(page, new LambdaQueryWrapper<Message>()
                .eq(Message::getConversationNo, conversationDTO.getConversationNo())
                .lt(Message::getCreatedAt, conversationDTO.getBefore())
                .orderByAsc(Message::getCreatedAt));
        List<MessageVO> messageVOs = messagePage.getRecords().stream()
                .map(message -> {
                    MessageVO vo = new MessageVO();
                    BeanUtil.copyProperties(message, vo);
                    //设置sender
                    UserVO2 sender2 = userClient.queryUserById(message.getSenderId());
                    UserVO sender = BeanUtil.copyProperties(sender2, UserVO.class);
                    vo.setSender(sender);
                    //设置article
                    if(message.getArticleNo() != null && !message.getArticleNo().isEmpty()){
                        ArticleVO article = articleClient.getArticleByNo(message.getArticleNo());
                        vo.setArticle(article);
                    }
                    //设置comment
                    if(message.getCommentNo() != null && !message.getCommentNo().isEmpty()){
                        CommentVO comment = commentClient.getCommentByNo(message.getCommentNo());
                        vo.setComment(comment);
                    }
                    return vo;
                })
                .toList();

        Page<MessageVO> voPage = new Page<>(1, queryLimit);
        voPage.setRecords(messageVOs);
        voPage.setTotal(messagePage.getTotal());
        return voPage;
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void markConversationRead(String conversationNo) {
        conversationMapper.update(null,new LambdaUpdateWrapper<Conversation>()
                .eq(Conversation::getConversationNo, conversationNo)
                .set(Conversation::getUnread, 0));
        messageMapper.update(null,new LambdaUpdateWrapper<Message>()
                .eq(Message::getConversationNo, conversationNo)
                .set(Message::getIsRead, true));
    }
}
