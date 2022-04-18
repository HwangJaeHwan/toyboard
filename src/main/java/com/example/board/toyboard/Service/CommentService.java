package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.CommentWriteDTO;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Repository.CommentRepository;
import com.example.board.toyboard.Repository.PostRepository;
import com.example.board.toyboard.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    public Comment findById(Long id) {

        return commentRepository.findById(id).orElse(null);
    }

    public Long writeComment(CommentWriteDTO dto, String nickname, Long postId) {

        User loginUser = userRepository.findByNickname(nickname).orElse(null);
        Post post = postRepository.findById(postId).orElse(null);

        Comment comment = Comment.builder()
                .user(loginUser)
                .comment(dto.getComment())
                .up(0)
                .down(0)
                .build();

        comment.setPost(post);

        Comment save = commentRepository.save(comment);

        return save.getId();

    }

    public List<Comment> findCommentsByPost(Post post) {

        return commentRepository.findCommentsByPost(post);
    }

}
