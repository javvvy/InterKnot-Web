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
@TableName("conversation")
public class Conversation {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String conversationNo;
    private String category;
    private String peerKey;
    private String peerName;
    private String peerAvatar;
    private String unread;
    private String lastPreview;
    private LocalDateTime lastAt;
    private String lastType;
}
