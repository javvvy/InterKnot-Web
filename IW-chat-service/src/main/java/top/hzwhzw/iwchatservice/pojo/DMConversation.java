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
@TableName("dm_conversation")
public class DMConversation {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String conversationNo;
    private String kind;
    private String title;  //非群聊为null
    private String avatar;
    private Long memberCount;
    private LocalDateTime lastMessageAt;
    private Long unreadCount;
    private String pseudoKind;
    private Long peerId;
    private String lastMessageNo;
    private Boolean muted;
    private Boolean pinned;
}
