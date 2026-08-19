package top.hzwhzw.iwchatservice.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import vo.KKCallConversationVO;

import java.util.List;

public interface KKCallService {
    List<KKCallConversationVO> getConversations();

    Object getSessionMessages(String conversationNo);

    SseEmitter sendMessage(String conversationNo, String content);
}
