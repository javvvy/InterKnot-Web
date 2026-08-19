package top.hzwhzw.iwchatservice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("kk_call_message")
public class KKCallMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String messageNo;
    private String conversationNo;
    private String role;
    private String content;
    private Boolean pending;
    private String errorReason;
    private LocalDateTime createdAt;
}
