package dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoverDTO {
    private String articleNo;
    private String url;
    private Integer width;
    private Integer height;
}
