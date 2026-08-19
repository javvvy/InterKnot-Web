package top.hzwhzw.iwchatservice.controller;

import dto.ConversationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pojo.Result;
import top.hzwhzw.iwchatservice.service.ConversationService;

@RestController
@RequestMapping("/knock")
@Slf4j
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;
    // 获取通知会话列表
    @GetMapping("/conversations")
    public Result getConversationList() {
        log.info("获取通知会话列表");
        return Result.success(conversationService.getConversationList());
    }
    // 获取指定通知会话消息
    @PostMapping("/conversations/{conversationNo}/messages")
    public Result getConversationMessages(@RequestBody ConversationDTO conversationDTO) {
        log.info("获取通知会话消息: {}", conversationDTO);
        return Result.success(conversationService.getConversationMessages(conversationDTO));
    }
    // 标记通知会话为已读
    @PostMapping("/conversations/{conversationNo}/mark-read")
    public Result markConversationRead(@PathVariable("conversationNo") String conversationNo) {
        conversationService.markConversationRead(conversationNo);
        log.info("已读通知会话: {}", conversationNo);
        return Result.successMsg("已读通知会话成功");
    }
}
