package top.hzwhzw.iwuserservice.pojo;

import cn.hutool.core.util.IdUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterData {
//    private Integer id;
    private String userNo = "user-"+ IdUtil.getSnowflakeNextIdStr();
    private String userName = "绳网用户"+ ThreadLocalRandom.current().nextInt(1, 1001);
    private String nickName = userName;
    private String avatar = "https://interknot-web.oss-cn-beijing.aliyuncs.com/default-avatar.jpg";
    private Long exp = 0L;
    private Long level = 1L;
    private Boolean profileHidden = false;
    private String bio = "这里什么都没有";
//    private String avatarUrl="https://interknot-web.oss-cn-beijing.aliyuncs.com/default-avatar.jpg";
//    private String avatarDocumentId=UUID.randomUUID().toString();
//    private String cardDocumentId=UUID.randomUUID().toString();
//    private String cardDescription="默认名片";
//    private String cardUrl="https://interknot-web.oss-cn-beijing.aliyuncs.com/main.jpg";
//    private String cardName="绳网";
}
