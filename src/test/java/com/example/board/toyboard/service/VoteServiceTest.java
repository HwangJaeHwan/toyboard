package com.example.board.toyboard.service;

import com.example.board.toyboard.entity.Comment;
import com.example.board.toyboard.entity.post.Post;
import com.example.board.toyboard.entity.post.PostType;
import com.example.board.toyboard.entity.User;
import com.example.board.toyboard.entity.UserType;
import com.example.board.toyboard.entity.log.Log;
import com.example.board.toyboard.entity.log.LogType;
import com.example.board.toyboard.entity.vote.Vote;
import com.example.board.toyboard.entity.vote.VoteType;
import com.example.board.toyboard.entity.vote.response.VoteResponse;
import com.example.board.toyboard.exception.CommentNotFoundException;
import com.example.board.toyboard.exception.UserNotFoundException;
import com.example.board.toyboard.repository.CommentRepository;
import com.example.board.toyboard.repository.LogRepository;
import com.example.board.toyboard.repository.UserRepository;
import com.example.board.toyboard.repository.VoteRepository;
import com.example.board.toyboard.repository.post.PostDocumentRepository;
import com.example.board.toyboard.repository.post.PostRepository;
import com.example.board.toyboard.response.ApiResponse;
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
class VoteServiceTest {

    @Autowired
    private VoteService voteService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private PostRepository postRepository;

    @MockBean
    PostDocumentRepository postDocumentRepository;

    @MockBean
    PostRedisService redisService;

    @AfterEach
    void clean() {
        logRepository.deleteAllInBatch();
        voteRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("처음 UP 투표를 하면 Vote와 Log가 저장되고 UP 개수를 반환한다")
    @Test
    void vote_firstUpVote_saveVoteAndLog() {
        // given
        User user = saveUser("tester");
        Post post = savePost(user);
        Comment comment = saveComment(user, post);

        // when
        ApiResponse<VoteResponse> result = voteService.vote(user.getNickname(), comment.getId(), VoteType.UP);

        // then
        assertThat(result.getMessage()).isEqualTo("UP 완료");
        assertThat(result.getData().getVoteType()).isEqualTo(VoteType.UP);
        assertThat(result.getData().getCount()).isEqualTo(1);

        List<Vote> votes = voteRepository.findAll();
        assertThat(votes).hasSize(1);
        assertThat(votes.get(0).getUser().getId()).isEqualTo(user.getId());
        assertThat(votes.get(0).getComment().getId()).isEqualTo(comment.getId());
        assertThat(votes.get(0).getVoteType()).isEqualTo(VoteType.UP);

        List<Log> logs = logRepository.findAll();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getUser().getId()).isEqualTo(user.getId());
        assertThat(logs.get(0).getComment().getId()).isEqualTo(comment.getId());
        assertThat(logs.get(0).getPost().getId()).isEqualTo(post.getId());
        assertThat(logs.get(0).getLogType()).isEqualTo(LogType.UP);
    }

    @DisplayName("처음 DOWN 투표를 하면 Vote와 Log가 저장되고 DOWN 개수를 반환한다")
    @Test
    void vote_firstDownVote_saveVoteAndLog() {
        // given
        User user = saveUser("tester");
        Post post = savePost(user);
        Comment comment = saveComment(user, post);

        // when
        ApiResponse<VoteResponse> result = voteService.vote(user.getNickname(), comment.getId(), VoteType.DOWN);

        // then
        assertThat(result.getMessage()).isEqualTo("DOWN 완료");
        assertThat(result.getData().getVoteType()).isEqualTo(VoteType.DOWN);
        assertThat(result.getData().getCount()).isEqualTo(1);

        List<Vote> votes = voteRepository.findAll();
        assertThat(votes).hasSize(1);
        assertThat(votes.get(0).getVoteType()).isEqualTo(VoteType.DOWN);

        List<Log> logs = logRepository.findAll();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getLogType()).isEqualTo(LogType.DOWN);
    }

    @DisplayName("같은 타입으로 다시 투표하면 기존 투표가 취소되고 로그도 삭제된다")
    @Test
    void vote_sameTypeAgain_cancelVoteAndDeleteLog() {
        // given
        User user = saveUser("tester");
        Post post = savePost(user);
        Comment comment = saveComment(user, post);

        voteRepository.save(new Vote(user, comment, VoteType.UP));
        logRepository.save(new Log(user, post, LogType.UP, comment));

        // when
        ApiResponse<VoteResponse> result = voteService.vote(user.getNickname(), comment.getId(), VoteType.UP);

        // then
        assertThat(result.getMessage()).isEqualTo("해당 투표를 취소합니다.");
        assertThat(result.getData().getVoteType()).isEqualTo(VoteType.UP);
        assertThat(result.getData().getCount()).isEqualTo(0);

        assertThat(voteRepository.findAll()).isEmpty();
        assertThat(logRepository.findAll()).isEmpty();
    }

    @DisplayName("반대 타입으로 이미 투표한 상태에서 요청하면 현재 코드 기준 메시지만 반환하고 투표는 변경되지 않는다")
    @Test
    void vote_alreadyVotedOppositeType_returnMessageOnly() {
        // given
        User user = saveUser("tester");
        Post post = savePost(user);
        Comment comment = saveComment(user, post);

        voteRepository.save(new Vote(user, comment, VoteType.DOWN));
        logRepository.save(new Log(user, post, LogType.DOWN, comment));

        // when
        ApiResponse<VoteResponse> result = voteService.vote(user.getNickname(), comment.getId(), VoteType.UP);

        // then
        assertThat(result.getMessage()).isEqualTo("이미 DOWN한 댓글입니다.");
        assertThat(result.getData().getVoteType()).isEqualTo(VoteType.UP);
        assertThat(result.getData().getCount()).isEqualTo(0);

        List<Vote> votes = voteRepository.findAll();
        assertThat(votes).hasSize(1);
        assertThat(votes.get(0).getVoteType()).isEqualTo(VoteType.DOWN);

        List<Log> logs = logRepository.findAll();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getLogType()).isEqualTo(LogType.DOWN);
    }

    @DisplayName("다른 사용자의 같은 댓글 UP 투표는 개수에 함께 반영된다")
    @Test
    void vote_countIncludesOtherUsersVotes() {
        // given
        User user1 = saveUser("tester1");
        User user2 = saveUser("tester2");
        Post post = savePost(user1);
        Comment comment = saveComment(user1, post);

        voteRepository.save(new Vote(user2, comment, VoteType.UP));

        // when
        ApiResponse<VoteResponse> result = voteService.vote(user1.getNickname(), comment.getId(), VoteType.UP);

        // then
        assertThat(result.getMessage()).isEqualTo("UP 완료");
        assertThat(result.getData().getCount()).isEqualTo(2);

        assertThat(voteRepository.findAll()).hasSize(2);
        assertThat(voteRepository.countByCommentAndVoteType(comment, VoteType.UP)).isEqualTo(2);
    }

    @DisplayName("존재하지 않는 사용자가 투표하면 UserNotFoundException이 발생한다")
    @Test
    void vote_userNotFound_throwException() {
        // given
        User user = saveUser("writer");
        Post post = savePost(user);
        Comment comment = saveComment(user, post);

        // when & then
        assertThatThrownBy(() -> voteService.vote("없는유저", comment.getId(), VoteType.UP))
                .isInstanceOf(UserNotFoundException.class);

        assertThat(voteRepository.findAll()).isEmpty();
        assertThat(logRepository.findAll()).isEmpty();
    }

    @DisplayName("존재하지 않는 댓글에 투표하면 CommentNotFoundException이 발생한다")
    @Test
    void vote_commentNotFound_throwException() {
        // given
        User user = saveUser("tester");

        // when & then
        assertThatThrownBy(() -> voteService.vote(user.getNickname(), 999999L, VoteType.UP))
                .isInstanceOf(CommentNotFoundException.class);

        assertThat(voteRepository.findAll()).isEmpty();
        assertThat(logRepository.findAll()).isEmpty();
    }

    @DisplayName("같은 타입 투표 취소 시 로그가 없어도 예외 없이 정상 처리된다")
    @Test
    void vote_cancelSameType_withoutLog_stillWorks() {
        // given
        User user = saveUser("tester");
        Post post = savePost(user);
        Comment comment = saveComment(user, post);

        voteRepository.save(new Vote(user, comment, VoteType.UP));

        // when
        ApiResponse<VoteResponse> result = voteService.vote(user.getNickname(), comment.getId(), VoteType.UP);

        // then
        assertThat(result.getMessage()).isEqualTo("해당 투표를 취소합니다.");
        assertThat(result.getData().getCount()).isEqualTo(0);

        assertThat(voteRepository.findAll()).isEmpty();
        assertThat(logRepository.findAll()).isEmpty();
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