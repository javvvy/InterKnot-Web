package top.hzwhzw.iwarticleservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import dto.ArticleLikesDTO;
import dto.ReadsDTO;
import top.hzwhzw.iwarticleservice.pojo.Article;
import vo.ArticlePageVO;
import vo.ArticleVO;
import vo.ReadsVO;

import java.util.List;

public interface ArticleService extends IService<Article> {
    IPage<ArticlePageVO> pageList(Integer page, Integer pageSize);

    ArticleVO detail(String articleNo);

    void view(String articleNo);

    void deleteArticle(String articleNo);

    List<ReadsVO> selectReads(ReadsDTO readsDTO,String userNo);

    void markReads(ReadsDTO readsDTO, String userNo);

    Long like(String articleNo);

    Object likes(ArticleLikesDTO articleLikesDTO);

    IPage<ArticlePageVO> pageListByUserNo(String userNo, Integer page, Integer pageSize);
}
