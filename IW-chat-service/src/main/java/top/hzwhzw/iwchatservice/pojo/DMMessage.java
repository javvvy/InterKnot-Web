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
@TableName("dm_message")
public class DMMessage {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String messageNo;
    private String conversationNo;
    private String content;
    private LocalDateTime createdAt;
    private String kind;
    private Long senderUserId;
}