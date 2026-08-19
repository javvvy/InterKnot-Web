package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DMMessageVO {
    private String messageNo;
    private String content;
    private LocalDateTime createdAt;
    private String kind;
    private UserVO2 sender;
    //是否为自己发送的消息
    private Boolean isSelf;
}
