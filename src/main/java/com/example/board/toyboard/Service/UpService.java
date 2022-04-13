package com.example.board.toyboard.Service;


import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Up;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Repository.CommentRepository;
import com.example.board.toyboard.Repository.UpRepository;
import com.example.board.toyboard.Repository.UserRepository;
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
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    public void upClick(User user, Comment comment) {

        Optional<Up> check = upRepository.findByUserAndComment(user, comment);

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

    }


}
