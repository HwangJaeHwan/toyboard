package com.example.board.toyboard.Service;

import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Down;
import com.example.board.toyboard.Entity.Up;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Repository.DownRepository;
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


    public boolean downClick(User user, Comment comment) {

        Optional<Down> check = downRepository.findByUserAndComment(user, comment);

        if (upRepository.findByUserAndComment(user, comment).isPresent()) {
            return false;
        }

        if (check.isPresent()) {
            downRepository.delete(check.get());
            comment.subDown();
        } else {

            downRepository.save(
                    Down.builder()
                            .user(user)
                            .comment(comment)
                            .build()
            );
            comment.addDown();

        }

        return true;

    }



}
