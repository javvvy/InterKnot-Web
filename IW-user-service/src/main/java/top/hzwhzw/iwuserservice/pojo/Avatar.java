package top.hzwhzw.iwuserservice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("avatar")
public class Avatar {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String avatarNo;
    private Long userId;
    private Boolean equipped;
    //TODO 默认值
    private String url;
    private Integer width;
    private Integer height;
}
