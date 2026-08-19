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
@TableName("message")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String messageNo;
    private String conversationNo;
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private Long senderId;
    private String articleNo;
    private String commentNo;
}
