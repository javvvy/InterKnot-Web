package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    private String username;   //暂无用户名登录,无需传递,后续添加
    private String email;
    private String password;
    private String code;  //注册验证码, 登录时无需传递
}
