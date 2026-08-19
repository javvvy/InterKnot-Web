package top.hzwhzw.iwcommentservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import top.hzwhzw.iwcommentservice.mapper.CommentMapper;
import top.hzwhzw.iwcommentservice.pojo.Comment;
import top.hzwhzw.iwcommentservice.service.CommentService;
import top.hzwhzw.iwcommentservice.service.OpenService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vo.CommentVO;

@Service
@RequiredArgsConstructor
public class OpenServiceImpl implements OpenService {
    private final CommentMapper commentMapper;
    @Override
    public CommentVO getCommentByNo(String commentNo) {
        Comment comment = commentMapper.selectOne(new LambdaQueryWrapper<Comment>().eq(Comment::getCommentNo, commentNo));
        CommentVO commentVO = new CommentVO();
        BeanUtil.copyProperties(comment, commentVO);
        return commentVO;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(String articleNo) {
        commentMapper.delete(new LambdaQueryWrapper<Comment>().eq(Comment::getArticleNo, articleNo));
    }
}
