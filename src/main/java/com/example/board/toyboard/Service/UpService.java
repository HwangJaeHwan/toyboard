package com.example.board.toyboard.Service;


import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Up;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.CommentLog;
import com.example.board.toyboard.Entity.log.LogType;
import com.example.board.toyboard.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UpService {

    private final UpRepository upRepository;
    private final DownRepository downRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LogRepository logRepository;


    public boolean upClick(User user, Comment comment) {

        Optional<Up> check = upRepository.findByUserAndComment(user, comment);

        if (downRepository.findByUserAndComment(user, comment).isPresent()) {
            return false;
        }

        if (check.isPresent()) {
            Up up = check.get();
            comment.subUp(up);
            upRepository.delete(up);

            logRepository.findLogByUserAndCommentAndLogType(user, comment,LogType.UP).ifPresent(log -> {
                comment.removeLog(log);
                logRepository.delete(log);
            });



        } else {
            Post post = comment.getPost();

            Up up = Up.builder()
                    .user(user)
                    .comment(comment)
                    .build();


            comment.addUp(up);
            upRepository.save(up);



            CommentLog commentLog = new CommentLog(user, post, LogType.UP, comment);
            log.info("장난={}", comment);
            comment.addLog(commentLog);
            logRepository.save(commentLog);

        }

        return true;
    }


}
