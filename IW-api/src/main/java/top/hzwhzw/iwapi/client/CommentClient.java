package top.hzwhzw.iwapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.hzwhzw.iwapi.configuration.UserInfoFeignInterceptor;
import vo.CommentVO;

@FeignClient(value = "interknot-comment", path = "/comment",configuration = UserInfoFeignInterceptor.class)
public interface CommentClient {
    //通过commentNo获取comment
    @GetMapping("/getCommentByNo")
    CommentVO getCommentByNo(@RequestParam("commentNo") String commentNo);
    //删除指定文章的评论
    @GetMapping("/deleteComment")
    void deleteComment(@RequestParam("articleNo") String articleNo);
 }
