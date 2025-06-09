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
import java.util.Map;

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
                .post(post)
                .comment(dto.getContent())
                .build();


        commentRepository.save(comment);


        Log commentLog = new Log(loginUser, post, LogType.COMMENT, comment);

        comment.addLog(commentLog);
        logRepository.save(commentLog);

        return comment.getId();

    }

    public Long writeReply(CommentWriteDTO dto, String nickname, Long commentId) {

        User loginUser = userRepository.findByNickname(nickname).orElseThrow(UserNotFoundException::new);
        Comment parent = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);

        Comment comment = Comment.builder()
                .user(loginUser)
                .comment(dto.getContent())
                .post(parent.getPost())
                .parent(parent)
                .build();


        commentRepository.save(comment);


        Log commentLog = new Log(loginUser, parent.getPost(), LogType.COMMENT, comment);

        comment.addLog(commentLog);
        logRepository.save(commentLog);


        return comment.getId();

    }

    public void delete(Comment comment) {


            commentRepository.delete(comment);

    }

    public List<CommentReadDTO> findComments(Long postId) {

        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException();
        }

        List<Comment> comments = commentRepository.findCommentsByPost(postId);

        return getCommentReadDTOS(comments);


    }

    public PageDTO<CommentReportDTO> commentsWithReport(PageListDTO pageListDTO) {

        return new PageDTO<>(commentRepository.commentReports(pageListDTO.getPageable()));
    }


    public List<CommentReadDTO> getReplies(Long commentId) {

        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException();
        }

        List<Comment> replies = commentRepository.findRepliesByCommentId(commentId);

        return getCommentReadDTOS(replies);
    }

    private List<CommentReadDTO> getCommentReadDTOS(List<Comment> comments) {

        log.info("comments = {}", comments.size());

        List<Long> ids = comments.stream().map(Comment::getId).toList();
        log.info("ids={}", ids);
        Map<Long, Long> upCounts = commentRepository.getUpCounts(ids);
        Map<Long, Long> downCounts = commentRepository.getDownCounts(ids);
        Map<Long, Long> reportCounts = commentRepository.getReportCounts(ids);
        Map<Long, Long> replyCounts = commentRepository.getReplyCounts(ids);


        return comments.stream()
                .map(reply -> CommentReadDTO.builder()
                        .id(reply.getId())
                        .nickname(reply.getUser().getNickname())
                        .content(reply.getComment())
                        .up(upCounts.getOrDefault(reply.getId(), 0L))
                        .down(downCounts.getOrDefault(reply.getId(), 0L))
                        .report(reportCounts.getOrDefault(reply.getId(), 0L))
                        .reply(replyCounts.getOrDefault(reply.getId(), 0L))
                        .build()
                )
                .toList();
    }
}
