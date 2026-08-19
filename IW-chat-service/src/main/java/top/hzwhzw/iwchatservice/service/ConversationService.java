package top.hzwhzw.iwchatservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dto.ConversationDTO;
import top.hzwhzw.iwchatservice.pojo.Conversation;

public interface ConversationService extends IService<Conversation> {
    Object getConversationList();

    Object getConversationMessages(ConversationDTO conversationDTO);

    void markConversationRead(String conversationNo);
}
