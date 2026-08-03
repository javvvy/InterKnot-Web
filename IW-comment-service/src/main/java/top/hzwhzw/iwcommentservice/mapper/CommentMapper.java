package top.hzwhzw.iwcommentservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.hzwhzw.iwcommentservice.pojo.Comment;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
