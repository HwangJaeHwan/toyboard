package com.example.board.toyboard.service;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.entity.Comment;
import com.example.board.toyboard.entity.post.Post;
import com.example.board.toyboard.entity.post.PostType;
import com.example.board.toyboard.entity.report.CommentReport;
import com.example.board.toyboard.entity.User;
import com.example.board.toyboard.entity.UserType;
import com.example.board.toyboard.entity.log.Log;
import com.example.board.toyboard.entity.log.LogType;
import com.example.board.toyboard.entity.vote.Vote;
import com.example.board.toyboard.entity.vote.VoteType;
import com.example.board.toyboard.exception.CommentNotFoundException;
import com.example.board.toyboard.exception.PostNotFoundException;
import com.example.board.toyboard.exception.UserNotFoundException;
import com.example.board.toyboard.repository.CommentRepository;
import com.example.board.toyboard.repository.LogRepository;
import com.example.board.toyboard.repository.UserRepository;
import com.example.board.toyboard.repository.VoteRepository;
import com.example.board.toyboard.repository.post.PostDocumentRepository;
import com.example.board.toyboard.repository.post.PostRepository;
import com.example.board.toyboard.repository.report.CommentReportRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private CommentReportRepository commentReportRepository;

    @MockBean
    PostDocumentRepository postDocumentRepository;

    @MockBean
    PostRedisService redisService;

    @AfterEach
    void clean() {
        logRepository.deleteAllInBatch();
        voteRepository.deleteAllInBatch();
        commentReportRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("댓글 작성 시 댓글과 로그가 저장된다")
    @Test
    void writeComment_saveCommentAndLog() {
        // given
        User user = saveUser("tester");
        Post post = savePost(user);

        CommentWriteDTO dto = getCommentWriteDTO("댓글");

        // when
        Long commentId = commentService.writeComment(dto, user.getId(), post.getId());

        // then
        Comment savedComment = commentRepository.findById(commentId).orElseThrow();
        assertThat(savedComment.getComment()).isEqualTo("댓글");
        assertThat(savedComment.getUser().getId()).isEqualTo(user.getId());
        assertThat(savedComment.getPost().getId()).isEqualTo(post.getId());
        assertThat(savedComment.getParent()).isNull();

        List<Log> logs = logRepository.findAll();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getUser().getId()).isEqualTo(user.getId());
        assertThat(logs.get(0).getPost().getId()).isEqualTo(post.getId());
        assertThat(logs.get(0).getComment().getId()).isEqualTo(commentId);
        assertThat(logs.get(0).getLogType()).isEqualTo(LogType.COMMENT);
    }

    @DisplayName("댓글 작성 시 사용자가 없으면 UserNotFoundException이 발생한다")
    @Test
    void writeComment_userNotFound_throwException() {
        // given
        User user = saveUser("writer");
        Post post = savePost(user);

        CommentWriteDTO dto = getCommentWriteDTO("댓글");

        // when & then
        assertThatThrownBy(() -> commentService.writeComment(dto, 99999L, post.getId()))
                .isInstanceOf(UserNotFoundException.class);

        assertThat(commentRepository.findAll()).isEmpty();
        assertThat(logRepository.findAll()).isEmpty();
    }

    @DisplayName("댓글 작성 시 게시글이 없으면 PostNotFoundException이 발생한다")
    @Test
    void writeComment_postNotFound_throwException() {
        // given
        User user = saveUser("tester");

        CommentWriteDTO dto = getCommentWriteDTO("댓글");

        // when & then
        assertThatThrownBy(() -> commentService.writeComment(dto, user.getId(), 99999L))
                .isInstanceOf(PostNotFoundException.class);

        assertThat(commentRepository.findAll()).isEmpty();
        assertThat(logRepository.findAll()).isEmpty();
    }

    @DisplayName("대댓글 작성 시 부모 댓글을 참조하고 로그가 저장된다")
    @Test
    void writeReply_saveReplyAndLog() {
        // given
        User writer = saveUser("writer");
        User replier = saveUser("replier");
        Post post = savePost(writer);
        Comment parent = saveComment(writer, post, "부모 댓글");

        CommentWriteDTO dto = getCommentWriteDTO("대댓글");

        // when
        Long replyId = commentService.writeReply(dto, replier.getNickname(), parent.getId());

        // then
        Comment reply = commentRepository.findById(replyId).orElseThrow();
        assertThat(reply.getComment()).isEqualTo("대댓글");
        assertThat(reply.getUser().getId()).isEqualTo(replier.getId());
        assertThat(reply.getPost().getId()).isEqualTo(post.getId());
        assertThat(reply.getParent().getId()).isEqualTo(parent.getId());

        List<Log> logs = logRepository.findAll();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getUser().getId()).isEqualTo(replier.getId());
        assertThat(logs.get(0).getPost().getId()).isEqualTo(post.getId());
        assertThat(logs.get(0).getComment().getId()).isEqualTo(replyId);
        assertThat(logs.get(0).getLogType()).isEqualTo(LogType.COMMENT);
    }


    @DisplayName("대댓글 작성 시 사용자가 없으면 UserNotFoundException이 발생한다")
    @Test
    void writeReply_userNotFound_throwException() {
        // given
        User writer = saveUser("writer");
        Post post = savePost(writer);
        Comment parent = saveComment(writer, post, "부모 댓글");

        CommentWriteDTO dto = getCommentWriteDTO("대댓글");

        // when & then
        assertThatThrownBy(() -> commentService.writeReply(dto, "없는유저", parent.getId()))
                .isInstanceOf(UserNotFoundException.class);

        assertThat(commentRepository.findAll()).hasSize(1);
        assertThat(logRepository.findAll()).isEmpty();
    }

    @DisplayName("대댓글 작성 시 부모 댓글이 없으면 CommentNotFoundException이 발생한다")
    @Test
    void writeReply_parentCommentNotFound_throwException() {
        // given
        User replier = saveUser("replier");

        CommentWriteDTO dto = getCommentWriteDTO("대댓글");

        // when & then
        assertThatThrownBy(() -> commentService.writeReply(dto, replier.getNickname(), 99999L))
                .isInstanceOf(CommentNotFoundException.class);

        assertThat(commentRepository.findAll()).isEmpty();
        assertThat(logRepository.findAll()).isEmpty();
    }

    @DisplayName("작성자는 자신의 댓글을 삭제할 수 있다")
    @Test
    void delete_byWriter_success() {
        // given
        User writer = saveUser("writer");
        Post post = savePost(writer);
        Comment comment = saveComment(writer, post, "댓글");

        // when
        Long deletedId = commentService.delete(comment.getId(), writer.getId(), UserType.USER);

        // then
        assertThat(deletedId).isEqualTo(comment.getId());
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @DisplayName("관리자는 다른 사용자의 댓글도 삭제할 수 있다")
    @Test
    void delete_byAdmin_success() {
        // given
        User writer = saveUser("writer");
        Post post = savePost(writer);
        Comment comment = saveComment(writer, post, "댓글");

        // when
        Long deletedId = commentService.delete(comment.getId(), 99999L, UserType.ADMIN);

        // then
        assertThat(deletedId).isEqualTo(comment.getId());
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @DisplayName("작성자도 관리자도 아니면 댓글을 삭제할 수 없다")
    @Test
    void delete_notWriterOrAdmin_throwException() {
        // given
        User writer = saveUser("writer");
        User otherUser = saveUser("other");
        Post post = savePost(writer);
        Comment comment = saveComment(writer, post, "댓글");

        // when & then
        assertThatThrownBy(() -> commentService.delete(comment.getId(), otherUser.getId(), UserType.USER))
                .isInstanceOf(RuntimeException.class);

        assertThat(commentRepository.findById(comment.getId())).isPresent();
    }

    @DisplayName("삭제할 댓글이 없으면 CommentNotFoundException이 발생한다")
    @Test
    void delete_commentNotFound_throwException() {
        // when & then
        assertThatThrownBy(() -> commentService.delete(99999L, 1L, UserType.USER))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @DisplayName("게시글의 부모 댓글 목록을 조회하면 집계 정보와 함께 반환한다")
    @Test
    void findComments_returnCommentReadDTOList() {
        // given
        User writer = saveUser("writer");
        User upVoter1 = saveUser("upVoter1");
        User upVoter2 = saveUser("upVoter2");
        User downVoter = saveUser("downVoter");
        User reporter = saveUser("reporter");

        Post post = savePost(writer);
        Comment comment1 = saveComment(writer, post, "첫 번째 댓글");
        Comment comment2 = saveComment(writer, post, "두 번째 댓글");

        saveReply(upVoter1, post, comment1, "대댓글1");
        saveReply(upVoter2, post, comment1, "대댓글2");

        voteRepository.save(new Vote(upVoter1, comment1, VoteType.UP));
        voteRepository.save(new Vote(upVoter2, comment1, VoteType.UP));
        voteRepository.save(new Vote(downVoter, comment1, VoteType.DOWN));

        commentReportRepository.save(new CommentReport(reporter, comment1));

        // when
        List<CommentReadDTO> result = commentService.findComments(post.getId());

        // then
        assertThat(result).hasSize(2);

        CommentReadDTO target = result.stream()
                .filter(dto -> dto.getId().equals(comment1.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(target.getUserId()).isEqualTo(writer.getId());
        assertThat(target.getNickname()).isEqualTo(writer.getNickname());
        assertThat(target.getContent()).isEqualTo("첫 번째 댓글");
        assertThat(target.getUp()).isEqualTo(2L);
        assertThat(target.getDown()).isEqualTo(1L);
        assertThat(target.getReport()).isEqualTo(1L);
        assertThat(target.getReply()).isEqualTo(2L);

        CommentReadDTO noStats = result.stream()
                .filter(dto -> dto.getId().equals(comment2.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(noStats.getUp()).isEqualTo(0L);
        assertThat(noStats.getDown()).isEqualTo(0L);
        assertThat(noStats.getReport()).isEqualTo(0L);
        assertThat(noStats.getReply()).isEqualTo(0L);
    }

    @DisplayName("댓글이 없는 게시글의 댓글 목록 조회 시 빈 리스트를 반환한다")
    @Test
    void findComments_noComments_returnEmptyList() {
        // given
        User writer = saveUser("writer");
        Post post = savePost(writer);

        // when
        List<CommentReadDTO> result = commentService.findComments(post.getId());

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("대댓글 목록을 조회하면 해당 부모 댓글의 대댓글만 반환한다")
    @Test
    void getReplies_returnRepliesOnly() {
        // given
        User writer = saveUser("writer");
        User replier = saveUser("replier");
        Post post = savePost(writer);

        Comment parent1 = saveComment(writer, post, "부모1");
        Comment parent2 = saveComment(writer, post, "부모2");

        Comment reply1 = saveReply(replier, post, parent1, "부모1의 대댓글1");
        Comment reply2 = saveReply(replier, post, parent1, "부모1의 대댓글2");
        saveReply(replier, post, parent2, "부모2의 대댓글1");

        voteRepository.save(new Vote(writer, reply1, VoteType.UP));

        // when
        List<CommentReadDTO> result = commentService.getReplies(parent1.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(CommentReadDTO::getId)
                .containsExactlyInAnyOrder(reply1.getId(), reply2.getId());

        CommentReadDTO dto = result.stream()
                .filter(it -> it.getId().equals(reply1.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(dto.getContent()).isEqualTo("부모1의 대댓글1");
        assertThat(dto.getUp()).isEqualTo(1L);
        assertThat(dto.getDown()).isEqualTo(0L);
        assertThat(dto.getReport()).isEqualTo(0L);
        assertThat(dto.getReply()).isEqualTo(0L);
    }

    @DisplayName("대댓글이 없으면 빈 리스트를 반환한다")
    @Test
    void getReplies_noReplies_returnEmptyList() {
        // given
        User writer = saveUser("writer");
        Post post = savePost(writer);
        Comment parent = saveComment(writer, post, "부모 댓글");

        // when
        List<CommentReadDTO> result = commentService.getReplies(parent.getId());

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("신고가 10개 이상인 댓글만 페이지 형태로 조회한다")
    @Test
    void commentsWithReport_returnOnlyCommentsWithAtLeastTenReports() {
        // given
        User writer = saveUser("writer");
        Post post = savePost(writer);

        Comment reportedComment1 = saveComment(writer, post, "신고 댓글1");
        Comment reportedComment2 = saveComment(writer, post, "신고 댓글2");
        Comment notEnoughReportedComment = saveComment(writer, post, "신고 부족 댓글");

        // 신고 10개 이상
        for (int i = 1; i <= 10; i++) {
            User reporter = saveUser("reporterA" + i);
            commentReportRepository.save(new CommentReport(reporter, reportedComment1));
        }

        for (int i = 1; i <= 12; i++) {
            User reporter = saveUser("reporterB" + i);
            commentReportRepository.save(new CommentReport(reporter, reportedComment2));
        }

        // 신고 10개 미만
        for (int i = 1; i <= 9; i++) {
            User reporter = saveUser("reporterC" + i);
            commentReportRepository.save(new CommentReport(reporter, notEnoughReportedComment));
        }

        PageListDTO pageListDTO = new PageListDTO();

        // when
        PageDTO<CommentReportDTO> result = commentService.commentsWithReport(pageListDTO);

        // then
        assertThat(result.getDtoList()).hasSize(2);

        assertThat(result.getDtoList())
                .extracting(CommentReportDTO::getId)
                .containsExactlyInAnyOrder(reportedComment1.getId(), reportedComment2.getId());

        assertThat(result.getDtoList())
                .extracting(CommentReportDTO::getReposts)
                .allMatch(count -> count >= 10L);
    }

    private CommentWriteDTO getCommentWriteDTO(String content) {

        CommentWriteDTO dto = new CommentWriteDTO();
        dto.setContent(content);

        return dto;

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

    private Comment saveComment(User user, Post post, String content) {
        return commentRepository.save(
                Comment.builder()
                        .comment(content)
                        .user(user)
                        .post(post)
                        .build()
        );
    }

    private Comment saveReply(User user, Post post, Comment parent, String content) {
        return commentRepository.save(
                Comment.builder()
                        .comment(content)
                        .user(user)
                        .post(post)
                        .parent(parent)
                        .build()
        );
    }
}