package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationVO {
    private String conversationNo;
    private String category;
    private String peerKey;
    private String peerName;
    private String peerAvatar;
    private String unread;
    private String lastPreview;
    private LocalDateTime lastAt;
    private String lastType;
}
