package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private String content;
    private String articleNo;  // 文章编号
    private String replyTo;  // 回复评论编号, 为空时为文章评论
}
