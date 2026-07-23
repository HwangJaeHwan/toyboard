package com.example.board.toyboard.service;


import com.example.board.toyboard.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


//    public UpResponse upClick(Long userId, Long commentId) {
//
//        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
//        Comment comment = commentRepository.findCommentWithUps(commentId).orElseThrow(CommentNotFoundException::new);
//
//
//        if (downRepository.existsByUserAndComment(user, comment)) {
//            return new UpResponse(true, null);
//        }
//
//        Optional<Up> existingUp = upRepository.findByUserAndComment(user, comment);
//
//        if (existingUp.isPresent()) {
//            Up up = existingUp.get();
//            comment.subUp(up);
//            logRepository.findLogByUserAndCommentAndLogType(user, comment, LogType.UP)
//                    .ifPresent(comment::removeLog);
//
//        } else {
//            Post post = comment.getPost();
//
//            Up up = Up.builder()
//                    .user(user)
//                    .comment(comment)
//                    .build();
//
//
//            comment.addUp(up);
//
//            Log commentLog = new Log(user, post, LogType.UP, comment);
//            comment.addLog(commentLog);
//
//        }
//
//        return new UpResponse(false, comment.getUps().size());
//    }


}
