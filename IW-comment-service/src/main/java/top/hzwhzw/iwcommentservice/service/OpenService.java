package top.hzwhzw.iwcommentservice.service;

import vo.CommentVO;

public interface OpenService {
    CommentVO getCommentByNo(String commentNo);

    void deleteComment(String articleNo);
}
