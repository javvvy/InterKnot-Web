package top.hzwhzw.iwcommentservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.hzwhzw.iwcommentservice.pojo.CommentLikes;

@Mapper
public interface LikesMapper extends BaseMapper<CommentLikes> {
}
