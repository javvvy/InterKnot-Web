package top.hzwhzw.iwarticleservice.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("cover")
public class Cover {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String coverNo;
    private String articleNo;    // 文章或草稿编号
    private String url;
    private Integer width;
    private Integer height;
}
