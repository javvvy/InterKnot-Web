package top.hzwhzw.iwarticleservice.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String articleNo;
    private String title;
    private String body;
    private String textHtml;
    private String rawBodyText;
    private Long views;
    private Long likesCount;
    private Long commentsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long authorId;
}
