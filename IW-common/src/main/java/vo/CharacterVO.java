package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacterVO {
    private String characterNo;
    private String name;
    private String avatar;
    private String tagline;
    private String tags;
    private Integer displayOrder;
}
