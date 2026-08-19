package top.hzwhzw.iwchatservice.controller;

import dto.DMConversationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import pojo.Result;
import top.hzwhzw.iwchatservice.service.DMService;
import utils.UserContextHolder;
import vo.DmSocketTicketVO;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/dm")
@RequiredArgsConstructor
public class DMController {
    private final DMService dmService;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String TICKET_PREFIX = "dm:socket:ticket:";
    //获取DM会话列表
    @GetMapping("/conversations")
    public Result getDMConversationList() {
        log.info("获取DM会话列表");
        return Result.success(dmService.getDMConversationList());
    }
    //查找/创建私聊对话
    @PostMapping("/conversations/direct")
    public Result createDMConversation(@RequestParam("targetUserNo") String targetUserNo) {
        log.info("查找/创建私聊对话");
        return Result.success(dmService.createDMConversation(targetUserNo));
    }
    //获取指定DM会话消息
    @PostMapping("/conversations/{conversationNo}/messages")
    public Result getDMConversationMessages(@RequestBody DMConversationDTO dmConversationDTO) {
        log.info("获取指定DM会话消息");
        return Result.success(dmService.getDMConversationMessages(dmConversationDTO));
    }
    //向指定对话发送消息
    @PostMapping("/conversations/{conversationNo}/send")
    public Result sendMessage(@PathVariable("conversationNo") String conversationNo,
                              @RequestBody DMConversationDTO dmConversationDTO) {
        log.info("向指定对话发送消息");
        return Result.success(dmService.sendMessage(conversationNo, dmConversationDTO));
    }
//    //编辑消息 暂不支持
//    @PatchMapping("/messages/{documentId}")
//    public Result updateMessage(@PathVariable String documentId,
//                                @RequestBody DMConversationDTO dmConversationDTO) {
//        log.info("编辑消息");
//        dmService.updateMessage(documentId, dmConversationDTO);
//        return Result.success();
//    }
    //撤回消息
    @DeleteMapping("/messages/{messageNo}")
    public Result withdrawMessage(@PathVariable("messageNo") String messageNo) {
        log.info("撤回消息");
        dmService.deleteMessage(messageNo);
        return Result.success();
    }
    //标记会话已读
    @PatchMapping("/conversations/{conversationNo}/read")
    public Result markConversationRead(@PathVariable("conversationNo") String conversationNo) {
        log.info("标记会话已读");
        dmService.markConversationRead(conversationNo);
        return Result.success();
    }
    //更新会话设置
    @PatchMapping("/conversations/{conversationNo}")
    public Result updateConversation(@PathVariable("conversationNo") String conversationNo,
                                    @RequestBody DMConversationDTO dmConversationDTO) {
        log.info("更新会话设置");
        dmService.updateDMConversation(conversationNo, dmConversationDTO);
        return Result.success();
    }
    //离开会话,仅群聊
    @DeleteMapping("/conversations/{conversationNo}/leave")
    public Result leaveConversation(@PathVariable("conversationNo") String conversationNo) {
        log.info("离开会话,仅群聊");
        dmService.deleteDMConversation(conversationNo);
        return Result.success();
    }
    //获取 WebSocket 连接 ticket
    @PostMapping("/socket/ticket")
    public Result getSocketTicket() {
        Long userId = UserContextHolder.getUserId();
        String ticket = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(TICKET_PREFIX + ticket, userId.toString(), 30, TimeUnit.SECONDS);
        log.info("生成 WebSocket ticket: userId={}", userId);
        return Result.success(new DmSocketTicketVO(ticket, 30));
    }
}
