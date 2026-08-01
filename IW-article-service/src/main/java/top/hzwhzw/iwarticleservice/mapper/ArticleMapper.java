package top.hzwhzw.iwarticleservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.hzwhzw.iwarticleservice.pojo.Article;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}
