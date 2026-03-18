package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.DownResponse;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Down;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.Log;
import com.example.board.toyboard.Entity.log.LogType;
import com.example.board.toyboard.Exception.CommentNotFoundException;
import com.example.board.toyboard.Exception.UserNotFoundException;
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
public class DownService {

    private final DownRepository downRepository;
    private final UpRepository upRepository;
    private final LogRepository logRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public DownResponse downClick(Long userId, Long commentId) {

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Comment comment = commentRepository.findCommentWithDowns(commentId).orElseThrow(CommentNotFoundException::new);


        if (upRepository.existsByUserAndComment(user, comment)) {
            return new DownResponse(true, null);
        }

        Optional<Down> existingDown = downRepository.findByUserAndComment(user, comment);
        if (existingDown.isPresent()) {
            Down down = existingDown.get();
            comment.subDown(down);
            logRepository.findLogByUserAndCommentAndLogType(user, comment, LogType.DOWN)
                    .ifPresent(comment::removeLog);
        } else {
            Post post = comment.getPost();

            Down down = Down.builder()
                    .user(user)
                    .comment(comment)
                    .build();

            comment.addDown(down);

            Log log = new Log(user, post, LogType.DOWN, comment);
            comment.addLog(log);
        }

        return new DownResponse(false, comment.getDowns().size());
    }


//    public boolean downClick(User user, Comment comment) {
//
//        Optional<Down> check = downRepository.findByUserAndComment(user, comment);
//
//        if (upRepository.findByUserAndComment(user, comment).isPresent()) {
//            return false;
//        }
//
//        if (check.isPresent()) {
//            Down down = check.get();
//            downRepository.delete(down);
//            logRepository.findLogByUserAndCommentAndLogType(user, comment,LogType.DOWN).ifPresent(log -> {
//                comment.removeLog(log);
//                logRepository.delete(log);
//            });
//            comment.subDown(down);
//        } else {
//            Post post = comment.getPost();
//
//            Down down = Down.builder()
//                    .user(user)
//                    .comment(comment)
//                    .build();
//
//            comment.addDown(down);
//            downRepository.save(down);
//
//            Log commentLog = new Log(user, post, LogType.DOWN, comment);
//            comment.addLog(commentLog);
//            logRepository.save(commentLog);
//
//        }
//
//        return true;
//
//    }





}
