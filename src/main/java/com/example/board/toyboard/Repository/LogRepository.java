package com.example.board.toyboard.Repository;

import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.Log;
import com.example.board.toyboard.Entity.log.LogType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LogRepository extends JpaRepository<Log, Long> {

    @Query(value = "select l from Log l join fetch l.post left join fetch l.comment where l.user = :user order by l.createdTime desc",
            countQuery = "select count(l) from Log l where l.user = :user")
    Page<Log> findLogsByUser(@Param("user") User user, Pageable pageable);

    @Query(value = "select l from Log l join l.post where l.user = :user and l.logType = :logType order by l.createdTime desc")
    Page<Log> findPostLogsByUser(@Param("user") User user, @Param("logType") LogType logType, Pageable pageable);

    Optional<Log> findLogByUserAndCommentAndLogType(User user, Comment comment, LogType type);

    Optional<Log> findLogByUserAndPostAndLogType(User user, Post post,LogType logType);

    void deleteAllByPost(Post post);

    void deleteAllByComment(Comment comment);


}
