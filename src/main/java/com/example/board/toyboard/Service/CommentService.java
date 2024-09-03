package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.Log;
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

    private final LogRepository logRepository;


    public Comment findById(Long id) {

        return commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
    }

    public Comment findByIdWithUser(Long id) {

        return commentRepository.findByIdWithUser(id).orElseThrow(CommentNotFoundException::new);
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
                .build();


        commentRepository.save(comment);


        Log commentLog = new Log(loginUser, post, LogType.COMMENT, comment);

        comment.addLog(commentLog);
        logRepository.save(commentLog);

        return comment.getId();

    }

    public void delete(Comment comment) {


            commentRepository.delete(comment);

    }

    public List<Comment> findComments(Long postId) {

        return commentRepository.findCommentsByPost(postId);

    }

    public PageDTO<CommentReportDTO> commentsWithReport(PageListDTO pageListDTO) {

        return new PageDTO<>(commentRepository.commentReports(pageListDTO.getPageable()));
    }

    public List<CommentReadDTO> test(Post post) {
        return commentRepository.commentInfo(post);
    }


}
