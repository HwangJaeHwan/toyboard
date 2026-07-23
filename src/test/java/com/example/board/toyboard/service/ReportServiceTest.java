package com.example.board.toyboard.service;



import com.example.board.toyboard.entity.Comment;
import com.example.board.toyboard.entity.post.Post;
import com.example.board.toyboard.entity.post.PostType;
import com.example.board.toyboard.entity.report.CommentReport;
import com.example.board.toyboard.entity.report.PostReport;
import com.example.board.toyboard.entity.User;
import com.example.board.toyboard.entity.UserType;
import com.example.board.toyboard.exception.CommentNotFoundException;
import com.example.board.toyboard.exception.PostNotFoundException;
import com.example.board.toyboard.repository.CommentRepository;
import com.example.board.toyboard.repository.UserRepository;
import com.example.board.toyboard.repository.post.PostDocumentRepository;
import com.example.board.toyboard.repository.post.PostRepository;
import com.example.board.toyboard.repository.report.CommentReportRepository;
import com.example.board.toyboard.repository.report.PostReportRepository;
import com.example.board.toyboard.response.ApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private PostReportRepository postReportRepository;

    @Autowired
    private CommentReportRepository commentReportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @MockBean
    PostDocumentRepository postDocumentRepository;

    @MockBean
    PostRedisService redisService;

    @AfterEach
    void clean() {
        commentReportRepository.deleteAllInBatch();
        postReportRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("게시글을 신고하면 PostReport가 저장되고 신고 완료 메시지를 반환한다")
    @Test
    void postReport_success() {
        // given
        User reporter = saveUser("reporter");
        User writer = saveUser("writer");
        Post post = savePost(writer);

        // when
        ApiResponse<Long> result = reportService.postReport(reporter.getId(), post.getId());

        // then
        assertThat(result.getMessage()).isEqualTo("신고 완료");
        assertThat(result.getData()).isNotNull();

        List<PostReport> reports = postReportRepository.findAll();
        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).getUser().getId()).isEqualTo(reporter.getId());
        assertThat(reports.get(0).getPost().getId()).isEqualTo(post.getId());
        assertThat(result.getData()).isEqualTo(reports.get(0).getId());
    }

    @DisplayName("이미 신고한 게시글을 다시 신고하면 저장하지 않고 이미 신고한 게시물 메시지를 반환한다")
    @Test
    void postReport_duplicateReport_returnAlreadyReportedMessage() {
        // given
        User reporter = saveUser("reporter");
        User writer = saveUser("writer");
        Post post = savePost(writer);

        postReportRepository.save(new PostReport(reporter, post));

        // when
        ApiResponse<Long> result = reportService.postReport(reporter.getId(), post.getId());

        // then
        assertThat(result.getMessage()).isEqualTo("이미 신고한 게시물입니다.");
        assertThat(result.getData()).isEqualTo(post.getId());

        List<PostReport> reports = postReportRepository.findAll();
        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).getUser().getId()).isEqualTo(reporter.getId());
        assertThat(reports.get(0).getPost().getId()).isEqualTo(post.getId());
    }


    @DisplayName("게시글 신고 시 존재하지 않는 게시글이면 예외가 발생한다")
    @Test
    void postReport_postNotFound() {
        // given
        User reporter = saveUser("reporter");

        // when & then
        assertThatThrownBy(() -> reportService.postReport(reporter.getId(), 99999L))
                .isInstanceOf(PostNotFoundException.class);

        assertThat(postReportRepository.findAll()).isEmpty();
    }


    @DisplayName("댓글을 처음 신고하면 CommentReport가 저장되고 신고 완료 메시지를 반환한다")
    @Test
    void saveCommentReport_success() {
        // given
        User reporter = saveUser("reporter");
        User writer = saveUser("writer");
        Post post = savePost(writer);
        Comment comment = saveComment(writer, post);

        // when
        ApiResponse<Long> result = reportService.commentReport(reporter.getNickname(), comment.getId());

        // then
        assertThat(result.getMessage()).isEqualTo("신고 완료");
        assertThat(result.getData()).isNotNull();

        List<CommentReport> reports = commentReportRepository.findAll();
        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).getUser().getId()).isEqualTo(reporter.getId());
        assertThat(reports.get(0).getComment().getId()).isEqualTo(comment.getId());
        assertThat(result.getData()).isEqualTo(reports.get(0).getId());
    }

    @DisplayName("이미 신고한 댓글을 다시 신고하면 저장하지 않고 이미 신고한 댓글 메시지를 반환한다")
    @Test
    void commentReport_duplicateReport() {
        // given
        User reporter = saveUser("reporter");
        User writer = saveUser("writer");
        Post post = savePost(writer);
        Comment comment = saveComment(writer, post);

        commentReportRepository.save(new CommentReport(reporter, comment));

        // when
        ApiResponse<Long> result = reportService.commentReport(reporter.getNickname(), comment.getId());

        // then
        assertThat(result.getMessage()).isEqualTo("이미 신고한 댓글입니다.");
        assertThat(result.getData()).isEqualTo(comment.getId());

        List<CommentReport> reports = commentReportRepository.findAll();
        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).getUser().getId()).isEqualTo(reporter.getId());
        assertThat(reports.get(0).getComment().getId()).isEqualTo(comment.getId());
    }


    @DisplayName("댓글 신고 시 존재하지 않는 댓글이면 예외가 발생한다")
    @Test
    void commentReport_commentNotFound() {
        // given
        User reporter = saveUser("reporter");

        // when & then
        assertThatThrownBy(() -> reportService.commentReport(reporter.getNickname(), 99999L))
                .isInstanceOf(CommentNotFoundException.class);

        assertThat(commentReportRepository.findAll()).isEmpty();
    }



    private User saveUser(String nickname) {
        return userRepository.save(
                User.builder()
                        .loginId(nickname + "Id")
                        .password("1234")
                        .nickname(nickname)
                        .email(nickname + "@test.com")
                        .userType(UserType.USER)
                        .build()
        );
    }

    private Post savePost(User user) {
        return postRepository.save(
                Post.builder()
                        .title("게시글 제목")
                        .content("게시글 내용")
                        .postType(PostType.FREE)
                        .hits(0)
                        .user(user)
                        .build()
        );
    }

    private Comment saveComment(User user, Post post) {
        return commentRepository.save(
                Comment.builder()
                        .comment("댓글 내용")
                        .user(user)
                        .post(post)
                        .build()
        );
    }
}