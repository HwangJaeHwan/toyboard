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

//    @Transactional
//    public DownResponse downClick(Long userId, Long commentId) {
//
//        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
//        Comment comment = commentRepository.findCommentWithDowns(commentId).orElseThrow(CommentNotFoundException::new);
//
//
//<<<<<<< Updated upstream
//        if (upRepository.existsByUserAndComment(user, comment)) {
//            return new DownResponse(true, null);
//        }
//
//        Optional<Down> existingDown = downRepository.findByUserAndComment(user, comment);
//        if (existingDown.isPresent()) {
//            Down down = existingDown.get();
//            comment.subDown(down);
//            logRepository.findLogByUserAndCommentAndLogType(user, comment, LogType.DOWN)
//                    .ifPresent(comment::removeLog);
//        } else {
//            Post post = comment.getPost();
//
//            Down down = Down.builder()
//                    .user(user)
//                    .comment(comment)
//                    .build();
//
//            comment.addDown(down);
//
//            Log log = new Log(user, post, LogType.DOWN, comment);
//            comment.addLog(log);
//        }
//
//        return new DownResponse(false, comment.getDowns().size());
//=======
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
//
//    public boolean downClick(User user, Comment comment) {
//
//        // 1️⃣ 추천이 이미 있으면 실패
//        if (upRepository.existsByUserAndComment(user, comment)) {
//            return false;
//        }
//
//        Optional<Down> existingDown = downRepository.findByUserAndComment(user, comment);
//
//        // 2️⃣ 이미 비추천이 있으면 취소
//        if (existingDown.isPresent()) {
//            cancelDown(existingDown.get(), user, comment);
//            return true;
//        }
//
//        // 3️⃣ 비추천 추가
//        addDown(user, comment);
//
//        return true;
//    }
//
//    private void addDown(User user, Comment comment) {
//
//        Down down = Down.builder()
//                .user(user)
//                .comment(comment)
//                .build();
//
//        comment.addDown(down);
//        downRepository.save(down);
//
//        createDownLog(user, comment);
//    }
//
//    private void cancelDown(Down down, User user, Comment comment) {
//
//        downRepository.delete(down);
//        comment.subDown(down);
//
//        deleteDownLog(user, comment);
//    }
//
//    private void createDownLog(User user, Comment comment) {
//
//        Post post = comment.getPost();
//
//        Log log = new Log(user, post, LogType.DOWN, comment);
//
//        comment.addLog(log);
//        logRepository.save(log);
//    }
//
//    private void deleteDownLog(User user, Comment comment) {
//
//        logRepository
//                .findLogByUserAndCommentAndLogType(user, comment, LogType.DOWN)
//                .ifPresent(log -> {
//                    comment.removeLog(log);
//                    logRepository.delete(log);
//                });
//>>>>>>> Stashed changes
//    }


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
