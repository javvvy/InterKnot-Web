package top.hzwhzw.iwcommentservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.hzwhzw.iwcommentservice.service.OpenService;
import vo.CommentVO;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Slf4j
public class OpenController {
    private final OpenService openService;
    //通过commentNo获取comment
    @GetMapping("/getCommentByNo")
    CommentVO getCommentByNo(@RequestParam("commentNo") String commentNo){
        log.info("getCommentByNo: {}", commentNo);
        return openService.getCommentByNo(commentNo);
    }
    //删除指定文章的评论
    @GetMapping("/deleteComment")
    void deleteComment(@RequestParam("articleNo") String articleNo){
        log.info("deleteComment: {}", articleNo);
        openService.deleteComment(articleNo);
    }
}
