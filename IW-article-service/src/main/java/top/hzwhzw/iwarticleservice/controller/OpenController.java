package top.hzwhzw.iwarticleservice.controller;

import dto.CoverDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pojo.Result;
import top.hzwhzw.iwarticleservice.service.OpenService;
import vo.ArticleVO;
import vo.CoverVO;

@Slf4j
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class OpenController {
    private final OpenService openService;
    //插入封面
    @PostMapping("/insertCover")
    public CoverVO insertCover(@RequestBody CoverDTO coverDTO) {
        return openService.insertCover(coverDTO);
    }
    // 通过文章编号查询文章
    @PostMapping("/getArticleByNo")
    public ArticleVO getArticleByNo(@RequestParam("articleNo") String articleNo) {
        return openService.getArticleByNo(articleNo);
    }
}
