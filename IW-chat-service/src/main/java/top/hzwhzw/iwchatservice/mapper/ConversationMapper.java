package top.hzwhzw.iwchatservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.hzwhzw.iwchatservice.pojo.Conversation;

@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {
}
