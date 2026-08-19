package top.hzwhzw.iwarticleservice.service;

import dto.CoverDTO;
import vo.ArticleVO;
import vo.CoverVO;

public interface OpenService {
    CoverVO insertCover(CoverDTO coverDTO);

    ArticleVO getArticleByNo(String articleNo);
}
