package top.hzwhzw.iwarticleservice.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pojo.PageResult;
import pojo.Result;
import top.hzwhzw.iwarticleservice.pojo.Article;
import top.hzwhzw.iwarticleservice.service.ArticleService;

@Slf4j
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    @GetMapping
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("分页查询所有文章列表");
        PageResult<Article> pageResult = articleService.pageList(page, pageSize);
        return Result.success(pageResult);
    }
}
