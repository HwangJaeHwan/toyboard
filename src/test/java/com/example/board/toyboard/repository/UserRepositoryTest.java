package com.example.board.toyboard.repository;

import com.example.board.toyboard.DTO.UserSearch;
import com.example.board.toyboard.DTO.UserSearchType;
import com.example.board.toyboard.entity.User;
import com.example.board.toyboard.entity.UserType;
import com.example.board.toyboard.repository.post.PostDocumentRepository;
import com.example.board.toyboard.service.PostRedisService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @MockBean
    PostDocumentRepository postDocumentRepository;

    @MockBean
    PostRedisService redisService;

    @DisplayName("닉네임으로 사용자를 검색한다")
    @Test
    void searchByNickname() {
        // given
        User user1 = saveUser("login1", "springUser");
        User user2 = saveUser("login2", "javaUser");
        User user3 = saveUser("login3", "springAdmin");

        UserSearch search = new UserSearch(UserSearchType.NICKNAME, "spring");


        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<User> result = userRepository.search(pageable, search);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        assertThat(result.getContent())
                .extracting(User::getNickname)
                .containsExactly("springAdmin", "springUser");
    }

    @DisplayName("로그인 아이디로 사용자를 검색한다")
    @Test
    void searchByLoginId() {
        // given
        User user1 = saveUser("spring1", "사용자1");
        User user2 = saveUser("java1", "사용자2");
        User user3 = saveUser("spring2", "사용자3");

        UserSearch search = new UserSearch(UserSearchType.LOGIN_ID,"spring");

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<User> result = userRepository.search(pageable, search);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        assertThat(result.getContent())
                .extracting(User::getLoginId)
                .containsExactly("spring2", "spring1");
    }

    @DisplayName("검색 타입이 없으면 전체 사용자를 최신순으로 조회한다")
    @Test
    void searchWithoutType() {
        // given
        User user1 = saveUser("login1", "사용자1");
        User user2 = saveUser("login2", "사용자2");
        User user3 = saveUser("login3", "사용자3");

        UserSearch search = new UserSearch(null,"spring");

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<User> result = userRepository.search(pageable, search);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);

        assertThat(result.getContent())
                .extracting(User::getLoginId)
                .containsExactly("login3", "login2", "login1");
    }

    @DisplayName("사용자를 페이지 크기만큼 조회한다")
    @Test
    void searchPaging() {
        // given
        saveUser("login1", "사용자1");
        saveUser("login2", "사용자2");
        saveUser("login3", "사용자3");

        UserSearch search = new UserSearch();

        Pageable pageable = PageRequest.of(0, 2);

        // when
        Page<User> result = userRepository.search(pageable, search);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    private User saveUser(String loginId, String nickname) {
        return userRepository.save(
                User.builder()
                        .loginId(loginId)
                        .password("1234")
                        .nickname(nickname)
                        .email(loginId + "@test.com")
                        .userType(UserType.USER)
                        .build()
        );
    }
}