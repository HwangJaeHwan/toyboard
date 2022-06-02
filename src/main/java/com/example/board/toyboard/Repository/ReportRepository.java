package com.example.board.toyboard.Repository;

import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Report.CommentReport;
import com.example.board.toyboard.Entity.Report.PostReport;
import com.example.board.toyboard.Entity.Report.Report;
import com.example.board.toyboard.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report,Long> {

    Optional<CommentReport> findByUserAndComment(User user, Comment comment);

    Optional<PostReport> findByUserAndPost(User user, Post post);
}
