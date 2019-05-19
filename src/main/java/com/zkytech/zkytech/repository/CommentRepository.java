package com.zkytech.zkytech.repository;

import com.zkytech.zkytech.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    int countCommentsByArticleIdAndAndTargetId(Long articleId,Long targetId);   // 用于楼层计数
    int countCommentsByArticleIdAndFloor(Long articleId, int floor);

    List<Comment> findAllByArticleId(Long articleId, Sort sort);
    Page<Comment> findAllByUserId(Long userId, Pageable pageable);
    Page<Comment> findAllByTargetUserId(Long targetUserId, Pageable pageable);
    Comment findCommentById(Long id);

    @Transactional
    void deleteCommentByid(Long id);
    @Transactional
    void deleteCommentsByArticleId(Long articleId);

    int countCommentsByTargetUserIdAndReadIsFalse(Long targetUserId);
    List<Comment> findCommentsByTargetUserIdAndReadIsFalse(Long targetUserId);
}
