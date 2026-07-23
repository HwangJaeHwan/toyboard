package com.example.board.toyboard.service;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.entity.Comment;
import com.example.board.toyboard.entity.post.Post;
import com.example.board.toyboard.entity.post.PostType;
import com.example.board.toyboard.entity.User;
import com.example.board.toyboard.entity.UserType;
import com.example.board.toyboard.entity.log.Log;
import com.example.board.toyboard.entity.log.LogType;
import com.example.board.toyboard.exception.*;
import com.example.board.toyboard.repository.CommentRepository;
import com.example.board.toyboard.repository.LogRepository;
import com.example.board.toyboard.repository.UserRepository;
import com.example.board.toyboard.repository.post.PostDocumentRepository;
import com.example.board.toyboard.repository.post.PostRepository;
import com.example.board.toyboard.session.UserSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    LogRepository logRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    PostDocumentRepository postDocumentRepository;

    @MockBean
    PostRedisService redisService;



    @AfterEach
    void clean() {
        logRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }


    @DisplayName("회원가입이 정상적으로 수행된다")
    @Test
    void save_success() {
        // given
        RegisterDTO dto = RegisterDTO.builder()
                .loginId("testId")
                .password("1234")
                .passwordCheck("1234")
                .nickname("tester")
                .email("test@test.com")
                .build();

        // when
        Long userId = userService.save(dto);

        // then
        User savedUser = userRepository.findById(userId).orElseThrow();

        assertThat(savedUser.getLoginId()).isEqualTo("testId");
        assertThat(savedUser.getNickname()).isEqualTo("tester");
        assertThat(savedUser.getEmail()).isEqualTo("test@test.com");
        assertThat(savedUser.getUserType()).isEqualTo(UserType.USER);
        assertThat(passwordEncoder.matches("1234", savedUser.getPassword())).isTrue();
    }

    @DisplayName("회원가입 시 비밀번호 확인이 다르면 예외가 발생한다")
    @Test
    void save_fail_passwordMismatch() {
        // given
        RegisterDTO dto = RegisterDTO.builder()
                .loginId("testId")
                .password("1234")
                .passwordCheck("9999")
                .nickname("tester")
                .email("test@test.com")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.save(dto))
                .isInstanceOf(PasswordMismatchException.class);

        assertThat(userRepository.findByLoginId("testId")).isEmpty();
    }

    @DisplayName("회원가입 시 로그인 아이디가 중복되면 예외가 발생한다")
    @Test
    void save_fail_duplicateLoginId() {
        // given
        userRepository.save(
                User.builder()
                        .loginId("testId")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("tester1")
                        .email("test1@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        RegisterDTO dto = RegisterDTO.builder()
                .loginId("testId")
                .password("1234")
                .passwordCheck("1234")
                .nickname("tester2")
                .email("test2@test.com")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.save(dto))
                .isInstanceOf(DuplicateLoginIdException.class);
    }

    @DisplayName("회원가입 시 닉네임이 중복되면 예외가 발생한다")
    @Test
    void save_fail_duplicateNickname() {
        // given
        userRepository.save(
                User.builder()
                        .loginId("testId1")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("tester")
                        .email("test1@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        RegisterDTO dto = RegisterDTO.builder()
                .loginId("testId2")
                .password("1234")
                .passwordCheck("1234")
                .nickname("tester")
                .email("test2@test.com")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.save(dto))
                .isInstanceOf(DuplicateNicknameException.class);
    }

    @DisplayName("회원가입 시 이메일이 중복되면 예외가 발생한다")
    @Test
    void save_fail_duplicateEmail() {
        // given
        userRepository.save(
                User.builder()
                        .loginId("testId1")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("tester1")
                        .email("test@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        RegisterDTO dto = RegisterDTO.builder()
                .loginId("testId2")
                .password("1234")
                .passwordCheck("1234")
                .nickname("tester2")
                .email("test@test.com")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.save(dto))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @DisplayName("회원가입 시 중복 우선순위는 로그인 아이디가 먼저 검사된다")
    @Test
    void save_fail_duplicateLoginId_first() {
        // given
        userRepository.save(
                User.builder()
                        .loginId("testId")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("tester")
                        .email("test@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        RegisterDTO dto = RegisterDTO.builder()
                .loginId("testId")
                .password("1234")
                .passwordCheck("1234")
                .nickname("tester")
                .email("test@test.com")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.save(dto))
                .isInstanceOf(DuplicateLoginIdException.class);
    }

    @DisplayName("로그인 성공 시 UserSession을 반환한다")
    @Test
    void login_success() {
        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testId")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("tester")
                        .email("test@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        LoginDTO dto = new LoginDTO();
        dto.setLoginId("testId");
        dto.setPassword("1234");

        // when
        UserSession result = userService.login(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getNickname()).isEqualTo("tester");
        assertThat(result.getUserType()).isEqualTo(UserType.USER);
    }

    @DisplayName("로그인 시 비밀번호가 틀리면 예외가 발생한다")
    @Test
    void login_fail_wrongPassword() {
        // given
        userRepository.save(
                User.builder()
                        .loginId("testId")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("tester")
                        .email("test@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        LoginDTO dto = new LoginDTO();
        dto.setLoginId("testId");
        dto.setPassword("wrongPassword");

        // when & then
        assertThatThrownBy(() -> userService.login(dto))
                .isInstanceOf(LoginFailException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지 않습니다.");
    }

    @DisplayName("로그인 시 존재하지 않는 아이디면 예외가 발생한다")
    @Test
    void login_fail_userNotFound() {
        // given
        LoginDTO dto = new LoginDTO();
        dto.setLoginId("notExistId");
        dto.setPassword("1234");

        // when & then
        assertThatThrownBy(() -> userService.login(dto))
                .isInstanceOf(LoginFailException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지 않습니다.");
    }


    @DisplayName("닉네임 변경이 정상적으로 수행된다")
    @Test
    void nicknameChange_success() {
        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testId")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("oldNickname")
                        .email("test@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        UserSession userSession = UserSession.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .userType(user.getUserType())
                .build();

        UserEditDTO dto = new UserEditDTO("newNickname");

        // when
        UserSession result = userService.nicknameChange(userSession, dto);

        // then
        User changedUser = userRepository.findById(user.getId()).orElseThrow();

        assertThat(changedUser.getNickname()).isEqualTo("newNickname");
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getNickname()).isEqualTo("newNickname");
        assertThat(result.getUserType()).isEqualTo(UserType.USER);
    }

    @DisplayName("닉네임 변경 시 닉네임이 중복되면 예외가 발생한다")
    @Test
    void nicknameChange_fail_duplicateNickname() {
        // given
        User user1 = userRepository.save(
                User.builder()
                        .loginId("testId1")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("nickname1")
                        .email("test1@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        userRepository.save(
                User.builder()
                        .loginId("testId2")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("nickname2")
                        .email("test2@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        UserSession userSession = UserSession.builder()
                .id(user1.getId())
                .nickname(user1.getNickname())
                .userType(user1.getUserType())
                .build();

        UserEditDTO dto = new UserEditDTO("nickname2");

        // when & then
        assertThatThrownBy(() -> userService.nicknameChange(userSession, dto))
                .isInstanceOf(DuplicateNicknameException.class);
    }



    @DisplayName("사용자의 전체 로그를 조회한다")
    @Test
    void findLogsByUser_success() {
        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testId")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("tester")
                        .email("test@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        Post post1 = postRepository.save(
                Post.builder()
                        .title("제목1")
                        .content("내용1")
                        .postType(PostType.FREE)
                        .hits(0)
                        .user(user)
                        .build()
        );

        Post post2 = postRepository.save(
                Post.builder()
                        .title("제목2")
                        .content("내용2")
                        .postType(PostType.FREE)
                        .hits(0)
                        .user(user)
                        .build()
        );


        Comment comment1 = commentRepository.save(
                Comment.builder()
                        .post(post1)
                        .comment("댓글1")
                        .user(user)
                        .build()
        );

        Comment comment2 = commentRepository.save(
                Comment.builder()
                        .post(post2)
                        .comment("댓글1")
                        .user(user)
                        .build()
        );


        logRepository.save(createPostLog(user, post1));
        logRepository.save(createPostLog(user, post2));
        logRepository.save(createCommentLog(user,comment1));
        logRepository.save(createCommentLog(user,comment2));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        MyPageDTO result = userService.findLogsByUser(user.getId(), pageable);

        // then
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getNickname()).isEqualTo("tester");
        assertThat(result.getPostNum()).isEqualTo(2L);
        assertThat(result.getLogs().getDtoList()).hasSize(4);
    }

    @DisplayName("사용자의 POST 타입 로그만 조회한다")
    @Test
    void findPostLogsByUser_success() {
        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testId")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("tester")
                        .email("test@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        Post post1 = postRepository.save(
                Post.builder()
                        .title("제목1")
                        .content("내용1")
                        .postType(PostType.FREE)
                        .hits(0)
                        .user(user)
                        .build()
        );

        Post post2 = postRepository.save(
                Post.builder()
                        .title("제목2")
                        .content("내용2")
                        .postType(PostType.FREE)
                        .hits(0)
                        .user(user)
                        .build()
        );

        logRepository.save(createPostLog(user, post1));
        logRepository.save(createPostLog(user, post2));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        MyPageDTO result = userService.findPostLogsByUser(user.getId(), pageable);

        // then
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getNickname()).isEqualTo("tester");
        assertThat(result.getPostNum()).isEqualTo(2L);
        assertThat(result.getLogs().getDtoList()).hasSize(2);
    }



    @DisplayName("비밀번호 변경이 정상적으로 수행된다")
    @Test
    void passwordChange_success() {
        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testId")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("tester")
                        .email("test@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        PasswordChangeDTO dto = PasswordChangeDTO.builder()
                .nowPassword("1234")
                .newPassword("5678")
                .newPasswordCheck("5678")
                .build();

        // when
        userService.passwordChange(user.getId(), dto);

        // then
        User changedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(passwordEncoder.matches("5678", changedUser.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("1234", changedUser.getPassword())).isFalse();
    }

    @DisplayName("비밀번호 변경 시 현재 비밀번호가 다르면 예외가 발생한다")
    @Test
    void passwordChange_fail_invalidNowPassword() {
        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testId")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("tester")
                        .email("test@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        PasswordChangeDTO dto = PasswordChangeDTO.builder()
                .nowPassword("wrongPassword")
                .newPassword("5678")
                .newPasswordCheck("5678")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.passwordChange(user.getId(), dto))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @DisplayName("비밀번호 변경 시 새 비밀번호와 비밀번호 확인이 다르면 예외가 발생한다")
    @Test
    void passwordChange_fail_passwordMismatch() {
        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testId")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("tester")
                        .email("test@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        PasswordChangeDTO dto = PasswordChangeDTO.builder()
                .nowPassword("1234")
                .newPassword("5678")
                .newPasswordCheck("9999")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.passwordChange(user.getId(), dto))
                .isInstanceOf(PasswordMismatchException.class);
    }

    @DisplayName("비밀번호 변경 시 현재 비밀번호와 새 비밀번호가 같으면 예외가 발생한다")
    @Test
    void passwordChange_fail_samePassword() {
        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testId")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("tester")
                        .email("test@test.com")
                        .userType(UserType.USER)
                        .build()
        );

        PasswordChangeDTO dto = PasswordChangeDTO.builder()
                .nowPassword("1234")
                .newPassword("1234")
                .newPasswordCheck("1234")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.passwordChange(user.getId(), dto))
                .isInstanceOf(SamePasswordException.class);
    }



    private Log createPostLog(User user, Post post) {
        return Log.builder()
                .user(user)
                .post(post)
                .logType(LogType.POST)
                .build();
    }

    private Log createCommentLog(User user, Comment comment) {
        return Log.builder()
                .user(user)
                .post(comment.getPost())
                .comment(comment)
                .logType(LogType.COMMENT)
                .build();
    }
}
