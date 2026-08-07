package top.hzwhzw.iwcommentservice.controller;

import dto.CommentDTO;
import dto.CommentLikesDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pojo.Result;
import top.hzwhzw.iwcommentservice.pojo.Comment;
import top.hzwhzw.iwcommentservice.service.CommentService;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    // 获取文章的评论列表
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "20") Integer pageSize,
                       @RequestParam String articleNo){
        log.info("获取文章{}的评论列表", articleNo);
        return Result.success(commentService.list(page, pageSize, articleNo));
    }
    // 获取评论的子评论列表
    @GetMapping("/replyList")
    public Result replyList(@RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            @RequestParam String commentNo){
        log.info("获取评论{}的子评论列表", commentNo);
        return Result.success(commentService.replyList(page, pageSize, commentNo));
    }
    //发表评论
    @PostMapping
    public Result create(@RequestBody CommentDTO comment){
        log.info("发表评论{}", comment);
        commentService.create(comment);
        return Result.successMsg("发表评论成功");
    }
    //删除评论
    @DeleteMapping("/{commentNo}")
    public Result delete(@PathVariable String commentNo){
        log.info("删除评论{}", commentNo);
        commentService.deleteComment(commentNo);
        return Result.successMsg("删除评论成功");
    }
    //点赞评论
    @PostMapping("/like")
    public Result like(@RequestParam String commentNo){
        log.info("点赞评论{}", commentNo);
        commentService.like(commentNo);
        return Result.successMsg("点赞评论成功");
    }
    //批量查询评论点赞状态
    @GetMapping("/batchLike")
    public Result batchLike(@RequestBody CommentLikesDTO commentLikesDTO){
        log.info("批量查询评论{}的点赞状态", commentLikesDTO);
        return Result.success(commentService.batchLike(commentLikesDTO));
    }
}
