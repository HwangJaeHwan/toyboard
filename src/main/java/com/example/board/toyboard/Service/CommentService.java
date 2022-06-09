package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.CommentWriteDTO;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.CommentLog;
import com.example.board.toyboard.Entity.log.LogType;
import com.example.board.toyboard.Exception.CommentNotFoundException;
import com.example.board.toyboard.Exception.PostNotFoundException;
import com.example.board.toyboard.Exception.UserNotFoundException;
import com.example.board.toyboard.Repository.CommentRepository;
import com.example.board.toyboard.Repository.LogRepository;
import com.example.board.toyboard.Repository.PostRepository;
import com.example.board.toyboard.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final LogRepository logRepository;


    public Comment findById(Long id) {

        return commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
    }

    public Comment findWithPostAndUser(Long id) {
        return commentRepository.findWithPostAndUser(id).orElseThrow(CommentNotFoundException::new);
    }

    public Long writeComment(CommentWriteDTO dto, String nickname, Long postId) {

        User loginUser = userRepository.findByNickname(nickname).orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        Comment comment = Comment.builder()
                .user(loginUser)
                .comment(dto.getContent())
                .post(post)
                .up(0)
                .down(0)
                .report(0)
                .build();


        commentRepository.save(comment);

        log.info("흐미={}", comment);

        CommentLog commentLog = new CommentLog(loginUser, post, LogType.COMMENT, comment);

        comment.addLog(commentLog);
        logRepository.save(commentLog);

        return comment.getId();

    }

    public void delete(Long id, User loginUser) {

        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);

        if (comment.getUser() == loginUser) {
            log.info("loginUser ={}, getUser ={}", loginUser, comment.getUser());

            commentRepository.delete(comment);
        } else {
            throw new IllegalStateException();
        }
    }

    public List<Comment> findComments(Post post) {

        return commentRepository.findCommentsByPost(post);

    }


}
