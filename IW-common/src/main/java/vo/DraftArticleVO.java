package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DraftArticleVO {
    private String draftNo;
    private String title;
    private String text;
    private List<CoverVO> covers;
    private Boolean hasPublishedVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
