package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardVO {
    private String cardNo;
    private String name;
    private String description;
    private String type;
    private Boolean equipped;
    private String url;
    private Integer width;
    private Integer height;
}
