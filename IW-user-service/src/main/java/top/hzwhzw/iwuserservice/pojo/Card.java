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
@TableName("card")
public class Card {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String cardNo;
    private Long userId;
    private String name;
    private String description;
    private String type;
    private Boolean equipped;
    private Integer width;
    private Integer height;
}
