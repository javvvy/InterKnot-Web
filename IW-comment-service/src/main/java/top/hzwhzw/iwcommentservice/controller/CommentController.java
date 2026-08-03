package top.hzwhzw.iwcommentservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
                            @RequestParam Long commentNo){
        log.info("获取评论{}的子评论列表", commentNo);
        return Result.success(commentService.replyList(page, pageSize, commentNo));
    }
}
