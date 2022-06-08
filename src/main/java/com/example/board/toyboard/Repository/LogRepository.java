package com.example.board.toyboard.Repository;

import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.CommentLog;
import com.example.board.toyboard.Entity.log.Log;
import com.example.board.toyboard.Entity.log.LogType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LogRepository extends JpaRepository<Log, Long> {


    Optional<CommentLog> findLogByUserAndCommentAndLogType(User user, Comment comment, LogType type);

    void deleteAllByPost(Post post);


}
