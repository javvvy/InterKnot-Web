package top.hzwhzw.iwarticleservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import dto.ArticleLikesDTO;
import dto.ReadsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pojo.PageResult;
import pojo.Result;
import top.hzwhzw.iwarticleservice.pojo.Article;
import top.hzwhzw.iwarticleservice.service.ArticleService;
import vo.ArticlePageVO;
import vo.ArticleVO;

@Slf4j
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    // 分页查询所有文章列表
    @GetMapping
    public Result list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        log.info("分页查询所有文章列表");
        IPage<ArticlePageVO> pageResult = articleService.pageList(page, pageSize);
        return Result.success(pageResult);
    }
    //TODO 根据关键词搜索文章列表

    // 查询文章详情
    @GetMapping("/detail/{articleNo}")
    public Result detail(@PathVariable("articleNo") String articleNo) {
        log.info("查询文章详情: {}", articleNo);
        return Result.success(articleService.detail(articleNo));
    }

    // 文章阅读量增加
    @PostMapping("/{articleNo}/view")
    public Result view(@PathVariable("articleNo") String articleNo) {
        log.info("文章阅读量增加: {}", articleNo);
        articleService.view(articleNo);
        return Result.success();
    }
    //删除文章
    @DeleteMapping("/{articleNo}")
    public Result delete(@PathVariable("articleNo") String articleNo){
        log.info("删除文章{}", articleNo);
        articleService.deleteArticle(articleNo);
        return Result.success();
    }
    //批量操作已读记录
    @PostMapping("/reads")
    public Result reads(@RequestBody ReadsDTO readsDTO,@RequestParam("userNo") String userNo){
        log.info("批量操作文章{}的已读记录,用户编号:{}", readsDTO.getArticleNos(),userNo);
        if(readsDTO.getMarkAsRead() == null || !readsDTO.getMarkAsRead()) {
            //批量查询已读记录
            return Result.success(articleService.selectReads(readsDTO,userNo));
        }else{
            //批量标记为已读
            articleService.markReads(readsDTO,userNo);
            return Result.successMsg("标记成功");
        }
    }
    //切换点赞状态
    @PostMapping("/{articleNo}/like")
    public Result like(@PathVariable("articleNo") String articleNo){
        log.info("切换文章{}的点赞状态", articleNo);
        return Result.success(articleService.like(articleNo));
    }
    //批量查询点赞状态
    @PostMapping("/likes")
    public Result likes(@RequestBody ArticleLikesDTO articleLikesDTO){
        log.info("批量查询文章{}的点赞状态", articleLikesDTO.getArticleNos());
        return Result.success(articleService.likes(articleLikesDTO));
    }

    //获取指定用户的文章列表
    @GetMapping("/profile/{userNo}")
    public Result userArticles(@PathVariable("userNo") String userNo,
                                @RequestParam(value = "page", defaultValue = "1") Integer page,
                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        log.info("获取用户{}的文章列表,分页参数:{} {}", page, pageSize, userNo);
        return Result.success(articleService.pageListByUserNo(userNo,page, pageSize));
    }
}