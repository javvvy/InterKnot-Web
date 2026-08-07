package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    private String commentNo;
    private Long likeCount;
    private String content;
    private CommentVO lastReply;
    private UserVO2 author;
    private LocalDateTime createdAt;
    private Boolean isLiked;
}
