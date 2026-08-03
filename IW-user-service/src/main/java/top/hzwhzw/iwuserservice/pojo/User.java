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
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String userNo;
    private String userName;
    private String nickName;
    private String passwordHash;
    private String email;
    private String avatar;
    private String role;
    private Long level;
    private Long exp;
    private String bio;
    private Boolean profileHidden;
}
