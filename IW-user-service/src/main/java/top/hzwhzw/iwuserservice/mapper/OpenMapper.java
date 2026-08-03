package top.hzwhzw.iwuserservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.hzwhzw.iwuserservice.pojo.User;

@Mapper
public interface OpenMapper extends BaseMapper<User> {
}
