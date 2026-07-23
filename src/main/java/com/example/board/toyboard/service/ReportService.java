package com.example.board.toyboard.service;


import com.example.board.toyboard.entity.Comment;
import com.example.board.toyboard.entity.post.Post;
import com.example.board.toyboard.entity.report.CommentReport;
import com.example.board.toyboard.entity.report.PostReport;
import com.example.board.toyboard.entity.User;
import com.example.board.toyboard.exception.CommentNotFoundException;
import com.example.board.toyboard.exception.PostNotFoundException;
import com.example.board.toyboard.exception.UserNotFoundException;
import com.example.board.toyboard.repository.CommentRepository;
import com.example.board.toyboard.repository.post.PostRepository;
import com.example.board.toyboard.repository.report.CommentReportRepository;
import com.example.board.toyboard.repository.report.PostReportRepository;
import com.example.board.toyboard.repository.UserRepository;
import com.example.board.toyboard.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ReportService {


    private final PostReportRepository postReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public ApiResponse<Long> postReport(Long userId , Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        return reportPost(user, post);

    }

    private ApiResponse<Long> reportPost(User user, Post post) {

        if (postReportRepository.existsByUserAndPost(user, post)) {
            return ApiResponse.success("이미 신고한 게시물입니다.", post.getId());
        }

        PostReport report = new PostReport(user, post);
        postReportRepository.save(report);

        return ApiResponse.success("신고 완료", report.getId());
    }


    public ApiResponse<Long> commentReport(String nickname , Long commentId) {

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        return reportComment(user, comment);

    }

    private ApiResponse<Long> reportComment(User user, Comment comment) {

        if (commentReportRepository.existsByUserAndComment(user, comment)) {
            return ApiResponse.success("이미 신고한 댓글입니다.",comment.getId());
        }

        CommentReport report = new CommentReport(user, comment);
        comment.getReports().add(report);

        commentReportRepository.save(report);

        return ApiResponse.success("신고 완료", report.getId());
    }




}
