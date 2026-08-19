package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KKCallConversationVO {
    private String conversationNo;
    private Boolean isPseudo;
    private CharacterVO character;
    private LocalDateTime lastMessageAt;
    private String lastPreview;
}
