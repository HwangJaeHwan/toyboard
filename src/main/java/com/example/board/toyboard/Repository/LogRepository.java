package com.example.board.toyboard.Repository;

import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.Log;
import com.example.board.toyboard.Entity.log.LogType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LogRepository extends JpaRepository<Log, Long> {

    @Query("select l from Log l join fetch l.post left join fetch l.comment where l.user = :user order by l.createdTime desc")
    List<Log> findLogsByUser(@Param("user") User user);
    Optional<Log> findLogByUserAndCommentAndLogType(User user, Comment comment, LogType type);

    Optional<Log> findLogByUserAndLogType(User user, LogType logType);

    void deleteAllByPost(Post post);


}
