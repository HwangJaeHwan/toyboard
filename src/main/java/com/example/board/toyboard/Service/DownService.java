package com.example.board.toyboard.Service;

import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Down;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Up;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.CommentLog;
import com.example.board.toyboard.Entity.log.LogType;
import com.example.board.toyboard.Repository.DownRepository;
import com.example.board.toyboard.Repository.LogRepository;
import com.example.board.toyboard.Repository.UpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class DownService {

    private final DownRepository downRepository;
    private final UpRepository upRepository;
    private final LogRepository logRepository;


    public boolean downClick(User user, Comment comment) {

        Optional<Down> check = downRepository.findByUserAndComment(user, comment);

        if (upRepository.findByUserAndComment(user, comment).isPresent()) {
            return false;
        }

        if (check.isPresent()) {
            Down down = check.get();
            downRepository.delete(down);
            logRepository.findLogByUserAndCommentAndLogType(user, comment,LogType.DOWN).ifPresent(log -> {
                comment.removeLog(log);
                logRepository.delete(log);
            });
            comment.subDown(down);
        } else {
            Post post = comment.getPost();

            Down down = Down.builder()
                    .user(user)
                    .comment(comment)
                    .build();

            comment.addDown(down);
            downRepository.save(down);

            CommentLog commentLog = new CommentLog(user, post, LogType.DOWN, comment);
            comment.addLog(commentLog);
            logRepository.save(commentLog);

        }

        return true;

    }



}
