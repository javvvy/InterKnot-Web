package top.hzwhzw.iwcommentservice.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dto.CommentDTO;
import dto.CommentLikesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.hzwhzw.iwapi.client.UserClient;
import top.hzwhzw.iwcommentservice.interceptor.UserContextInterceptor;
import top.hzwhzw.iwcommentservice.mapper.CommentMapper;
import top.hzwhzw.iwcommentservice.mapper.LikesMapper;
import top.hzwhzw.iwcommentservice.pojo.Comment;
import top.hzwhzw.iwcommentservice.pojo.CommentLikes;
import top.hzwhzw.iwcommentservice.service.CommentService;
import utils.UserContextHolder;
import vo.CommentLikesVO;
import vo.CommentVO;
import vo.UserVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    private final CommentMapper commentMapper;
    private final LikesMapper likesMapper;
    private final UserClient userClient;

    @Override
    public IPage<CommentVO> list(Integer pageNum, Integer pageSize, String articleNo) {
        // 1. 创建 Page 对象，传入当前页和每页条数
        Page<Comment> page = new Page<>(pageNum, pageSize);
        // 2. 调用分页查询方法，传入 Page 对象
        IPage<Comment> commentPage = commentMapper.selectPage(page, new LambdaQueryWrapper<Comment>().eq(Comment::getArticleNo, articleNo));
        // 如果为空，直接返回空结果
        if (commentPage.getRecords().isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }
        // 3. 转换 Comment 列表为 CommentVO 列表
        List<CommentVO> commentVOList = commentPage.getRecords().stream()
                .map(comment -> {
                    CommentVO commentVO = new CommentVO();
                    BeanUtils.copyProperties(comment, commentVO);
                    // 4. 组装作者
                    UserVO authorVO = userClient.queryUserByUserNo(comment.getAuthorNo());
                    commentVO.setAuthor(authorVO);
                    // 5. 设置点赞状态
                    if (UserContextHolder.getUserId() != null) {
                        commentVO.setIsLiked(liked(UserContextHolder.getUserId(), comment.getCommentNo()));
                    }
                    // 6. 设置最后回复
                    Comment lastReply = this.getOne(new LambdaQueryWrapper<Comment>()
                            .eq(Comment::getReplyTo, comment.getCommentNo())
                            // 按创建时间降序排列（最新的在前）
                            .orderByDesc(Comment::getCreatedAt)
                            // 限制只取第一条，提高查询效率
                            .last("limit 1"));
                    if (lastReply != null) {
                        CommentVO lastReplyVO = new CommentVO();
                        BeanUtils.copyProperties(lastReply, lastReplyVO);
                        // 7. 设置最后回复的点赞状态
                        if (UserContextHolder.getUserId() != null) {
                            lastReplyVO.setIsLiked(liked(UserContextHolder.getUserId(), lastReply.getCommentNo()));
                        }
                        // 8. 设置最后回复的作者
                        UserVO lastReplyAuthorVO = userClient.queryUserByUserNo(lastReply.getAuthorNo());
                        lastReplyVO.setAuthor(lastReplyAuthorVO);
                        commentVO.setLastReply(lastReplyVO);
                    }
                    return commentVO;
                })
                .collect(Collectors.toList());
        // 6. 创建新的 Page 对象，复制分页信息并设置转换后的数据
        Page<CommentVO> commentVOPage = new Page<>(pageNum, pageSize);
        commentVOPage.setRecords(commentVOList);
        commentVOPage.setTotal(commentPage.getTotal());
        commentVOPage.setCurrent(commentPage.getCurrent());
        commentVOPage.setSize(commentPage.getSize());
        commentVOPage.setPages(commentPage.getPages());
        // 7. 返回组装后的 CommentVO 对象
        return commentVOPage;
    }

    @Override
    public IPage<CommentVO> replyList(Integer pageNum, Integer pageSize, String commentNo) {
        // 1. 创建 Page 对象，传入当前页和每页条数
        Page<Comment> page = new Page<>(pageNum, pageSize);
        // 2. 调用分页查询方法，传入 Page 对象
        IPage<Comment> commentPage = commentMapper.selectPage(page, new LambdaQueryWrapper<Comment>().eq(Comment::getReplyTo, commentNo));
        // 如果为空，直接返回空结果
        if (commentPage.getRecords().isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }
        // 5. 转换子评论为 CommentVO
        List<CommentVO> replyVOList = commentPage.getRecords().stream().map(reply -> {
            CommentVO replyVO = new CommentVO();
            BeanUtils.copyProperties(reply, replyVO);
            // 4. 组装作者
            UserVO authorVO = userClient.queryUserByUserNo(reply.getAuthorNo());
            replyVO.setAuthor(authorVO);
            // 5. 设置点赞状态
            if (UserContextHolder.getUserId() != null) {
                replyVO.setIsLiked(liked(UserContextHolder.getUserId(), reply.getCommentNo()));
            }
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

    @Override
    public void create(CommentDTO comment) {
        Comment commentEntity = new Comment();
        //TODO 枚举类处理
        commentEntity.setArticleNo(comment.getArticleNo());
        commentEntity.setContent(comment.getContent());
        commentEntity.setAuthorNo(userClient.queryUserById(UserContextHolder.getUserId()).getUserNo());
        commentEntity.setCreatedAt(LocalDateTime.now());
        commentEntity.setCommentNo("comment-" + IdUtil.getSnowflakeNextIdStr());
        if (comment.getReplyTo() == null || comment.getReplyTo().isEmpty()) {
        } else {
            commentEntity.setReplyTo(comment.getReplyTo());
        }
        commentMapper.insert(commentEntity);
    }

    @Override
    public void deleteComment(String commentNo) {
        // 1.鉴权
        Comment comment = commentMapper.selectOne(new LambdaQueryWrapper<Comment>().eq(Comment::getCommentNo, commentNo));
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }
        if (!comment.getAuthorNo().equals(userClient.queryUserById(UserContextHolder.getUserId()).getUserNo())) {
            throw new IllegalArgumentException("您没有权限删除该评论");
        }
        // 2.删除评论
        commentMapper.delete(new LambdaQueryWrapper<Comment>().eq(Comment::getCommentNo, commentNo));
    }

    @Override
    public void like(String commentNo) {
        // 1.鉴权
        Comment comment = commentMapper.selectOne(new LambdaQueryWrapper<Comment>().eq(Comment::getCommentNo, commentNo));
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }
        // 2.切换点赞状态
        //查询是否点赞过
        boolean liked = liked(UserContextHolder.getUserId(), commentNo);
        if (liked) {
            // 2.1 取消点赞
            likesMapper.delete(new LambdaQueryWrapper<CommentLikes>()
                    .eq(CommentLikes::getUserId, UserContextHolder.getUserId())
                    .eq(CommentLikes::getCommentNo, commentNo));
        } else {
            // 2.2 点赞
            CommentLikes commentLikes = new CommentLikes();
            commentLikes.setUserId(UserContextHolder.getUserId());
            commentLikes.setCommentNo(commentNo);
            likesMapper.insert(commentLikes);
        }
       }


    @Override
    public List<CommentLikesVO> batchLike(CommentLikesDTO commentLikesDTO) {
        // 1. 验证评论是否存在
        List<Comment> commentList = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .in(Comment::getCommentNo, commentLikesDTO.getCommentNos())
        );
        if (commentList.isEmpty()) {
            throw new IllegalArgumentException("评论不存在");
        }
        // 2. 批量查询点赞状态
        List<CommentLikesVO> commentLikesVOList = commentList.stream()
                .map(comment -> {
                    CommentLikesVO commentLikesVO = new CommentLikesVO();
                    commentLikesVO.setCommentId(comment.getId());
                    commentLikesVO.setIsLiked(liked(UserContextHolder.getUserId(), comment.getCommentNo()));
                    return commentLikesVO;
                })
                .collect(Collectors.toList());
        return commentLikesVOList;
    }


    /**
     * 设置点赞状态
     */
    private boolean liked(Long userId, String commentNo) {
        return likesMapper.selectCount(
                new LambdaQueryWrapper<CommentLikes>()
                        .eq(CommentLikes::getUserId, userId)
                        .eq(CommentLikes::getCommentNo, commentNo)
        ) > 0;
    }
}