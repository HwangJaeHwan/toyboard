package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.CommentWriteDTO;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Exception.CommentNotFoundException;
import com.example.board.toyboard.Exception.PostNotFoundException;
import com.example.board.toyboard.Exception.UserNotFoundException;
import com.example.board.toyboard.Repository.CommentRepository;
import com.example.board.toyboard.Repository.PostRepository;
import com.example.board.toyboard.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

        return commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
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


        Comment save = commentRepository.save(comment);

        return save.getId();

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

    public List<Comment> findBestComments(Post post) {

        return commentRepository.findCommentsByPostAndUpGreaterThanEqual(post, 10, PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "up")));
    }

}
