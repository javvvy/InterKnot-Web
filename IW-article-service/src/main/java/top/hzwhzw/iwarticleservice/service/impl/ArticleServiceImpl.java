package top.hzwhzw.iwarticleservice.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pojo.PageResult;
import top.hzwhzw.iwarticleservice.mapper.ArticleMapper;
import top.hzwhzw.iwarticleservice.pojo.Article;
import top.hzwhzw.iwarticleservice.service.ArticleService;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    private final ArticleMapper articleMapper;
    @Override
    public PageResult<Article> pageList(Integer page, Integer pageSize) {
        return null;
    }
}
