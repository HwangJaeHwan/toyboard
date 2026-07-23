package com.example.board.toyboard.service;

import com.example.board.toyboard.DTO.HomePost;
import com.example.board.toyboard.entity.post.Post;
import com.example.board.toyboard.entity.post.PostType;
import com.example.board.toyboard.entity.User;
import com.example.board.toyboard.entity.UserType;
import com.example.board.toyboard.repository.UserRepository;
import com.example.board.toyboard.repository.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.example.board.toyboard.config.redis.RedisKey.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostRedisServiceTest {

    @Autowired
    private PostRedisService postRedisService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        // DB 정리
        postRepository.deleteAll();
        userRepository.deleteAll();

        // Redis 정리
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @DisplayName("같은 유저가 여저번 조회하도 조회수는 1번만 증가한다.")
    @Test
    void incrementViewCount() {
        // given
        User user = saveUser("user1");
        Post post = savePost(user, "제목1");

        // when
        postRedisService.incrementViewCount(post.getId(), user.getId());
        postRedisService.incrementViewCount(post.getId(), user.getId());
        postRedisService.incrementViewCount(post.getId(), user.getId());

        // then
        Object value = redisTemplate.opsForHash().get(REDIS_VIEW_COUNT_KEY, String.valueOf(post.getId()));
        assertThat(value).isEqualTo("1");

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String postViewerKey = REDIS_POST_VIEWERS_KEY_PREFIX + post.getId() + ":" + date;

        Long viewerCount = redisTemplate.opsForSet().size(postViewerKey);
        assertThat(viewerCount).isEqualTo(1L);
        assertThat(redisTemplate.hasKey(postViewerKey)).isTrue();
    }

    @DisplayName("서로 다른 유저가 조회하면 조회수는 유저 수만큼 증가한다")
    @Test
    void incrementViewCount_differentUsers() {
        // given
        User user1 = saveUser("user1");
        User user2 = saveUser("user2");
        User user3 = saveUser("user3");
        Post post = savePost(user1, "제목1");

        // when
        postRedisService.incrementViewCount(post.getId(), user1.getId());
        postRedisService.incrementViewCount(post.getId(), user2.getId());
        postRedisService.incrementViewCount(post.getId(), user3.getId());

        // then
        Object value = redisTemplate.opsForHash().get(REDIS_VIEW_COUNT_KEY, String.valueOf(post.getId()));
        assertThat(value).isEqualTo("3");
    }


    @DisplayName("가장 최근 글이 5개까지 앞에 저장된댜.")
    @Test
    void saveLatestPost() {
        // given
        for (long i = 1; i <= 7; i++) {
            postRedisService.saveLatestPost(PostType.FREE, i);
        }

        // when
        List<String> latestPosts = redisTemplate.opsForList()
                .range(REDIS_LATEST_KEY_PREFIX + PostType.FREE.name(), 0, -1);

        // then
        assertThat(latestPosts).containsExactly("7", "6", "5", "4", "3");
        assertThat(latestPosts).hasSize(5);
    }

    @DisplayName("게시판 타입별로 최신글을 Redis에 캐싱한다.")
    @Test
    void saveLatestPost_postType() {
        // given
        postRedisService.saveLatestPost(PostType.FREE, 1L);
        postRedisService.saveLatestPost(PostType.QNA, 2L);
        postRedisService.saveLatestPost(PostType.FREE, 3L);

        // when
        List<String> freePosts = redisTemplate.opsForList()
                .range(REDIS_LATEST_KEY_PREFIX + PostType.FREE.name(), 0, -1);

        List<String> qnaPosts = redisTemplate.opsForList()
                .range(REDIS_LATEST_KEY_PREFIX + PostType.QNA.name(), 0, -1);

        // then
        assertThat(freePosts).containsExactly("3", "1");
        assertThat(qnaPosts).containsExactly("2");
    }


    @DisplayName("인기글은 Redis 점수 순서대로 게시글을 반환한다")
    @Test
    void getPopularPost() {
        // given
        User user = saveUser("writer");

        Post post1 = savePost(user, "제목1");
        Post post2 = savePost(user, "제목2");
        Post post3 = savePost(user, "제목3");

        String top10Key = REDIS_POPULAR_POSTS_KEY_PREFIX + "top10";

        redisTemplate.opsForZSet().add(top10Key, String.valueOf(post1.getId()), 10);
        redisTemplate.opsForZSet().add(top10Key, String.valueOf(post2.getId()), 30);
        redisTemplate.opsForZSet().add(top10Key, String.valueOf(post3.getId()), 20);

        // when
        List<HomePost> result = postRedisService.getPopularPost();

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(HomePost::getId)
                .containsExactly(post2.getId(), post3.getId(), post1.getId());
    }


    @DisplayName("인기글은 10개를 초과해도 상위 10개만 조회한다")
    @Test
    void getPopularPost_top10() {
        // given
        User user = saveUser("writer");

        String top10Key = REDIS_POPULAR_POSTS_KEY_PREFIX + "top10";

        for (int i = 1; i <= 12; i++) {
            Post post = savePost(user, "제목" + i);
            redisTemplate.opsForZSet().add(top10Key, String.valueOf(post.getId()), i);
        }

        // when
        List<HomePost> result = postRedisService.getPopularPost();

        // then
        assertThat(result).hasSize(10);
    }

    private User saveUser(String loginId) {
        return userRepository.save(
                User.builder()
                        .loginId(loginId + UUID.randomUUID())
                        .password("1234")
                        .nickname(loginId)
                        .email(loginId + UUID.randomUUID() + "@test.com")
                        .userType(UserType.USER)
                        .build()
        );
    }

    private Post savePost(User user, String title) {
        return postRepository.save(
                Post.builder()
                        .title(title)
                        .content("내용")
                        .postType(PostType.FREE)
                        .hits(0)
                        .user(user)
                        .build()
        );
    }
}
