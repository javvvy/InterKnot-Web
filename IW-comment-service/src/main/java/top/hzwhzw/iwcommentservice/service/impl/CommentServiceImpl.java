package top.hzwhzw.iwcommentservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.hzwhzw.iwapi.client.UserClient;
import top.hzwhzw.iwcommentservice.interceptor.UserContextInterceptor;
import top.hzwhzw.iwcommentservice.mapper.CommentMapper;
import top.hzwhzw.iwcommentservice.pojo.Comment;
import top.hzwhzw.iwcommentservice.service.CommentService;
import utils.UserContextHolder;
import vo.CommentVO;
import vo.UserVO;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    private final CommentMapper commentMapper;
    private final UserClient userClient;
    private final UserContextInterceptor userContextInterceptor;

    @Override
    public IPage<CommentVO> list(Integer pageNum, Integer pageSize, String articleNo) {
        // 1. 创建 Page 对象，传入当前页和每页条数
        Page<Comment> page = new Page<>(pageNum, pageSize);
        // 2. 调用分页查询方法，传入 Page 对象
        IPage<Comment> commentPage = commentMapper.selectPage(page,new LambdaQueryWrapper<Comment>().eq(Comment::getArticleNo,articleNo));
        // 如果为空，直接返回空结果
        if (commentPage.getRecords().isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }
        // 3. 转换 Comment 列表为 CommentVO 列表
        List<CommentVO> commentVOList = commentPage.getRecords().stream()
                .map(comment -> {
                    CommentVO commentVO = new CommentVO();
                    BeanUtils.copyProperties(comment, commentVO);

                    // 6. 组装作者
                    UserVO authorVO = userClient.queryUserByUserNo(comment.getAuthorNo());
                    commentVO.setAuthor(authorVO);
                    //TODO 设置点赞状态
                    commentVO.setIsLiked(liked(comment.getAuthorNo(),comment.getId()));
                    return commentVO;
                })
                .collect(Collectors.toList());
        // 7. 创建新的 Page 对象，复制分页信息并设置转换后的数据
        Page<CommentVO> commentVOPage = new Page<>(pageNum, pageSize);
        commentVOPage.setRecords(commentVOList);
        commentVOPage.setTotal(commentPage.getTotal());
        commentVOPage.setCurrent(commentPage.getCurrent());
        commentVOPage.setSize(commentPage.getSize());
        commentVOPage.setPages(commentPage.getPages());
        // 返回组装后的 CommentVO 对象
        return commentVOPage;
    }
    @Override
    public IPage<CommentVO> replyList(Integer pageNum, Integer pageSize, Long commentNo) {
        // 1. 创建 Page 对象，传入当前页和每页条数
        Page<Comment> page = new Page<>(pageNum, pageSize);
        // 2. 调用分页查询方法，传入 Page 对象
        IPage<Comment> commentPage = commentMapper.selectPage(page,new LambdaQueryWrapper<Comment>().eq(Comment::getReplyTo,commentNo));
        // 如果为空，直接返回空结果
        if (commentPage.getRecords().isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }
        // 5. 转换子评论为 CommentVO
        List<CommentVO> replyVOList = commentPage.getRecords().stream().map(reply -> {
            CommentVO replyVO = new CommentVO();
            BeanUtils.copyProperties(reply, replyVO);
            return replyVO;
        }).collect(Collectors.toList());
        // 6. 创建新的 Page 对象，复制分页信息并设置转换后的数据
        Page<CommentVO> replyVOPage = new Page<>(pageNum, pageSize);
        replyVOPage.setRecords(replyVOList);
        replyVOPage.setTotal(commentPage.getTotal());
        replyVOPage.setCurrent(commentPage.getCurrent());
        replyVOPage.setSize(commentPage.getSize());
        replyVOPage.setPages(commentPage.getPages());
        // 返回组装后的 CommentVO 对象
        return replyVOPage;
    }


    /**
     * 设置点赞状态
     */
    private boolean liked(String userNo, Long commentId) {
        //TODO 更改为likesMapper
        return commentMapper.selectCount(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getAuthorNo, userNo)
                        .eq(Comment::getId, commentId)
        ) > 0;
    }
}
