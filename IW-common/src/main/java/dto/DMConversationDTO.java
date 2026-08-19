package dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DMConversationDTO {
    private String conversationNo;
    private Integer limit;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    private LocalDateTime before; //只查询在before时间之前的消息，使用 ISO-8601 标准字符串
    //发送消息字段
    private String content;
    private String kind = "text";
    //更新会话字段
    private Boolean muted;
    private Boolean pinned;
}
