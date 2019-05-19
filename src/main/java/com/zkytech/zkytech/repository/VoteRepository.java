package com.zkytech.zkytech.repository;

import com.zkytech.zkytech.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Vote findVoteByCommentIdAndAndUserId(Long commentId, Long userId);
    int countVotesByCommentIdAndAndUpvote(Long commentId, boolean upvote);
}
