package top.hzwhzw.iwchatservice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vo.CharacterVO;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("kk_call_conversation")
public class KKCallConversation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String conversationNo;
    private Long userId;
    private String characterNo;
    private Boolean isPseudo;
    private LocalDateTime lastMessageAt;
    private String lastPreview;
    private LocalDateTime createdAt;
}
