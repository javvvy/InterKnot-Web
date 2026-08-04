package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    private String commentNo;
    private Long likeCount;
    private String content;
    private CommentVO lastReply;
    private UserVO author;
    private LocalDateTime createdAt;
    private Boolean isLiked;
}
