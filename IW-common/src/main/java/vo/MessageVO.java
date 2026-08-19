package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageVO {
    private String messageNo;
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private UserVO sender;
    private ArticleVO article;
    private CommentVO comment;
}
