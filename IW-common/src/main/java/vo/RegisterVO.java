package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterVO {
    private String email;
    private Boolean sent;  //是否发送成功
    private Long expires;  //有效期(秒)
    private Long cooldown;  //冷却时间(秒)
}
