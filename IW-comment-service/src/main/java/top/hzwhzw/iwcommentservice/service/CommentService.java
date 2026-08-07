package top.hzwhzw.iwcommentservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import dto.CommentDTO;
import dto.CommentLikesDTO;
import top.hzwhzw.iwcommentservice.pojo.Comment;
import vo.CommentLikesVO;
import vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {
    public IPage<CommentVO> list(Integer page, Integer pageSize, String articleNo);

    public IPage< CommentVO >replyList(Integer page, Integer pageSize, String commentNo);

    void create(CommentDTO comment);

    void deleteComment(String commentNo);

    void like(String commentNo);

    List<CommentLikesVO> batchLike(CommentLikesDTO commentLikesDTO);
}
