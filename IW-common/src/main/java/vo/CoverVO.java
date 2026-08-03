package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoverVO {
    private Long id;
    private String coverNo;
    private String url;
    private String coverWidth;
    private String coverHeight;
}
