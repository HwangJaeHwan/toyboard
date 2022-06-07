package com.example.board.toyboard.Service;


import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Up;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.CommentLog;
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
            upRepository.delete(check.get());
            comment.subUp();
        } else {

            upRepository.save(
                    Up.builder()
                    .user(user)
                    .comment(comment)
                    .build()
            );
            comment.addUp();

        }

        return true;
    }


}
