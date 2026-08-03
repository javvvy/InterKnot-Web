package top.hzwhzw.iwarticleservice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("article_reads")
public class Reads {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String articleNo;
    private String userNo;
}
