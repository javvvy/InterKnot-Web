package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    private String commentNo;
    private List<CommentVO> replies;
    private Long likeCount;
    private String content;
    private UserVO author;
    private Long createTime;
    private Boolean isLiked;
}
