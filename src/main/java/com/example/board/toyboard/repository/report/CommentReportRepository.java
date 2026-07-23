package com.example.board.toyboard.repository.report;

import com.example.board.toyboard.entity.Comment;
import com.example.board.toyboard.entity.report.CommentReport;
import com.example.board.toyboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport,Long> {

    boolean existsByUserAndComment(User user, Comment comment);

}
