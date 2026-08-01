package top.hzwhzw.iwarticleservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pojo.PageResult;
import top.hzwhzw.iwarticleservice.pojo.Article;

public interface ArticleService extends IService<Article> {
    PageResult<Article> pageList(Integer page, Integer pageSize);
}
