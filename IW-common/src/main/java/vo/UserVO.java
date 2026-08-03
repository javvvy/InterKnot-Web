package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private Long id;
    private String userNo;
    private String nickName;
    private String avatar;
    private String bio;
    private String role;
    private Long level;
    private Long exp;
    private Boolean profileHidden;
}
