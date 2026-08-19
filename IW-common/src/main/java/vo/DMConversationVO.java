package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DMConversationVO {
    private String conversationNo;
    private String kind;
    private String title;
    private String avatar;
    private UserVO2 peer;
    private DMMessageVO lastMessage;
    private Long memberCount;
    private LocalDateTime lastMessageAt;
    private Long unreadCount;
    private String pseudoKind;
    private Boolean muted;
    private Boolean pinned;
    //TODO self:群聊相关字段
}
