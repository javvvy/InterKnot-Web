package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileVO {
    private String userNo;
    private String nickName;
    private String avatar;
    private CardVO card;
    private String bio;
    private Long level;
    private Long exp;
    private Boolean profileHidden;
}
