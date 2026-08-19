package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KKCallMessageVO {
    private String messageNo;
    private String role;
    private String content;
    private Boolean pending;
    private String errorReason;
    private LocalDateTime createdAt;
}
