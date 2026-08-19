package top.hzwhzw.iwchatservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.hzwhzw.iwchatservice.pojo.Message;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
