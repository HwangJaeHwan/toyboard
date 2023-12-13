package com.example.board.toyboard.Service;


import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Report.CommentReport;
import com.example.board.toyboard.Entity.Report.PostReport;
import com.example.board.toyboard.Entity.Report.Report;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Exception.CommentNotFoundException;
import com.example.board.toyboard.Exception.PostNotFoundException;
import com.example.board.toyboard.Exception.UserNotFoundException;
import com.example.board.toyboard.Repository.CommentRepository;
import com.example.board.toyboard.Repository.PostRepository;
import com.example.board.toyboard.Repository.ReportRepository;
import com.example.board.toyboard.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ReportService {


    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public boolean postReport(String nickname , Long postId) {
        User user = userRepository.findByNickname(nickname).orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        if (reportRepository.findByUserAndPost(user, post).isPresent()) {

            return false;

        }
        PostReport report = new PostReport(user, post);

        reportRepository.save(report);

        return true;

    }


    public boolean commentReport(String nickname , Long commentId) {

        User user = userRepository.findByNickname(nickname).orElseThrow(UserNotFoundException::new);
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);


        if (reportRepository.findByUserAndComment(user, comment).isPresent()) {
            return false;
        }

        CommentReport report = new CommentReport(user, comment);

        comment.getReports().add(report);

        reportRepository.save(report);

        return true;

    }




}
