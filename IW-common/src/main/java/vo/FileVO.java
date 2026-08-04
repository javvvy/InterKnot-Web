package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileVO {
    private String fileNo;
    private String url;
    private String fileName;
//    private String objectKey;
    private String fileType;
    private Long size;
    private Integer width;
    private Integer height;
    private LocalDateTime createdAt;
}
