package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KnockMeta {
    private Long total;
    private Boolean truncated;
    private Integer scannedRows;
    private Integer cap;
}
