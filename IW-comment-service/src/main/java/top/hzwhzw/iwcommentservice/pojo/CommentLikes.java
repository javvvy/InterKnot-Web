package top.hzwhzw.iwcommentservice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment_likes")
public class CommentLikes {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String commentNo;
    private Long userId;
}
