package top.hzwhzw.iwarticleservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import dto.DraftArticleDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pojo.Result;
import top.hzwhzw.iwarticleservice.pojo.DraftArticle;
import top.hzwhzw.iwarticleservice.service.DraftService;
import vo.DraftArticleVO;

@Slf4j
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class DraftController {
    private final DraftService draftService;
    //创建文章草稿
    @PostMapping
    public Result create(@RequestBody DraftArticleDTO draftArticle){
        log.info("创建文章草稿{}", draftArticle);;
        return Result.success(draftService.createDraftArticle(draftArticle));
    }
    //更新文章草稿
    @PutMapping("/{draftNo}")
    public Result update(@PathVariable String draftNo,@RequestBody DraftArticleDTO draftArticle){
        log.info("更新文章草稿{}", draftArticle);
        DraftArticle updated = draftService.updateDraftArticle(draftNo,draftArticle);
        return Result.success(updated);
    }
    //发布文章草稿
    @PostMapping("/{draftNo}/publish")
    public Result publish(@PathVariable String draftNo){
        log.info("发布文章草稿{}", draftNo);
        draftService.publishDraftArticle(draftNo);
        return Result.successMsg("发布成功");
    }
    //获取我的文章列表
    @GetMapping("/my")
    public Result myArticles(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("获取我的文章列表, page: {}, pageSize: {}", page, pageSize);
        IPage<DraftArticleVO> pageResult = draftService.myArticles(page, pageSize);
        return Result.success(pageResult);
    }
    //获取草稿详情
    @GetMapping("/my/{draftNo}")
    public Result draftDetail(@PathVariable String draftNo){
        log.info("获取草稿{}详情", draftNo);
        DraftArticleVO draft = draftService.getDraftByDraftNo(draftNo);
        return Result.success(draft);
    }
    //删除草稿
    @DeleteMapping("/my/draft/{draftNo}")
    public Result deleteDraft(@PathVariable String draftNo){
        log.info("删除草稿{}", draftNo);
        draftService.deleteDraftArticle(draftNo);
        return Result.successMsg("删除成功");
    }
}
