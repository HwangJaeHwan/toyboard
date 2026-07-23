package com.example.board.toyboard.repository;

import com.example.board.toyboard.entity.Comment;
import com.example.board.toyboard.entity.User;
import com.example.board.toyboard.entity.vote.Vote;
import com.example.board.toyboard.entity.vote.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {

    Optional<Vote> findByUserAndComment(User user, Comment comment);

    boolean existsByUserAndComment(User user, Comment comment);

    Long countByCommentAndVoteType(Comment comment, VoteType voteType);
}
