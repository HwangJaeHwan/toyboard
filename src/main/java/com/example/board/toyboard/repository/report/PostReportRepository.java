package com.example.board.toyboard.repository.report;

import com.example.board.toyboard.entity.post.Post;
import com.example.board.toyboard.entity.report.PostReport;
import com.example.board.toyboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport,Long> {

    boolean existsByUserAndPost(User user, Post post);
}
