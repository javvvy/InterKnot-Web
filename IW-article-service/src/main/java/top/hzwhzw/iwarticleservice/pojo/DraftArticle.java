package top.hzwhzw.iwarticleservice.pojo;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.cloud.openfeign.SpringQueryMap;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("draft_article")
public class DraftArticle {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String draftNo;
    private Long authorId;
    private String title;
    private String text;
    private Boolean hasPublishedVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
