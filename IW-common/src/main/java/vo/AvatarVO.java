package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvatarVO {
    private String avatarNo;
    private String url;
    private Boolean equipped;
    private Integer width;
    private Integer height;
}
