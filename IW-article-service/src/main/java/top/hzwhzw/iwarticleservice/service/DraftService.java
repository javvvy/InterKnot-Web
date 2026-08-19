package top.hzwhzw.iwarticleservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import dto.DraftArticleDTO;
import top.hzwhzw.iwarticleservice.pojo.DraftArticle;
import vo.DraftArticleVO;

public interface DraftService extends IService<DraftArticle> {
    DraftArticleVO createDraftArticle(DraftArticleDTO draftArticle);

    DraftArticleVO updateDraftArticle(String draftNo, DraftArticleDTO draftArticle);

    void publishDraftArticle(String draftNo);

    IPage<DraftArticleVO> myArticles(Integer page, Integer pageSize);

    DraftArticleVO getDraftByDraftNo(String draftNo);

    void deleteDraftArticle(String draftNo);

    void deleteDraftCover(String draftNo, String coverNo);
}
