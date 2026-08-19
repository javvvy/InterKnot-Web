package top.hzwhzw.iwchatservice.controller;


import dto.KkCallSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pojo.Result;
import top.hzwhzw.iwchatservice.service.KKCallService;

@RestController
@Slf4j
@RequestMapping("/kk-call")
public class KKCallController {
    @Autowired
    private KKCallService kkCallService;

    @GetMapping("/sessions")
    public Result getConversations() {
        log.info("获取通话会话列表");
        return Result.success(kkCallService.getConversations());
    }

    @GetMapping("/sessions/{conversationNo}/messages")
    public Result getSessionMessages(@PathVariable("conversationNo") String conversationNo) {
        log.info("获取通话会话消息历史: {}", conversationNo);
        return Result.success(kkCallService.getSessionMessages(conversationNo));
    }

    @PostMapping(value = "/sessions/{conversationNo}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessage(@PathVariable("conversationNo") String conversationNo,
                                  @RequestBody KkCallSendRequest request) {
        log.info("发送通话消息: conversationNo={}, content={}", conversationNo, request.getContent());
        return kkCallService.sendMessage(conversationNo, request.getContent());
    }
}

