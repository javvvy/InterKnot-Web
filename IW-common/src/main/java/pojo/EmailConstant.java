package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailConstant {
    // 邮箱验证码 Redis 前缀
    public static final String EMAIL_CODE_PREFIX = "email:code:";
    // 有效期 5 分钟
    public static final long CODE_EXPIRE = 5 * 60;
    // 发送冷却期 60 秒
    public static final long CODE_COOLDOWN = 60;
    // 6 位数字验证码
    public static final int CODE_LENGTH = 6;
}
