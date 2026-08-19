package top.hzwhzw.iwchatservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dto.DMConversationDTO;
import top.hzwhzw.iwchatservice.pojo.DMConversation;
import vo.DMConversationVO;

import java.util.List;

public interface DMService{
    List<DMConversationVO> getDMConversationList();

    Object createDMConversation(String targetUserNo);

    Object getDMConversationMessages(DMConversationDTO dmConversationDTO);

    Object sendMessage(String conversationNo, DMConversationDTO dmConversationDTO);

    void deleteMessage(String messageNo);

    void markConversationRead(String conversationNo);

    void updateDMConversation(String conversationNo, DMConversationDTO dmConversationDTO);

    void deleteDMConversation(String conversationNo);
}
