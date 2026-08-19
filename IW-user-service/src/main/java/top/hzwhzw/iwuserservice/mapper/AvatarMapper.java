package top.hzwhzw.iwuserservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.hzwhzw.iwuserservice.pojo.Avatar;

import java.util.List;

@Mapper
public interface AvatarMapper extends BaseMapper<Avatar> {
    int insertBatchSomeColumn(List<Avatar> avatarList);
}
