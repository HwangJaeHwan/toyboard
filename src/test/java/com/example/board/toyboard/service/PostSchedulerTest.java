package com.example.board.toyboard.service;

import com.example.board.toyboard.entity.post.Post;
import com.example.board.toyboard.entity.post.PostType;
import com.example.board.toyboard.entity.User;
import com.example.board.toyboard.entity.UserType;
import com.example.board.toyboard.repository.UserRepository;
import com.example.board.toyboard.repository.post.PostRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.example.board.toyboard.config.redis.RedisKey.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PostSchedulerTest {

    @Autowired
    PostScheduler postScheduler;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    @AfterEach
    void clearRedis() {
        redisTemplate.delete(REDIS_VIEW_COUNT_KEY);

        Set<String> popularKeys = redisTemplate.keys(REDIS_POPULAR_POSTS_KEY_PREFIX + "*");
        if (popularKeys != null) {
            redisTemplate.delete(popularKeys);
        }

        Set<String> latestKeys = redisTemplate.keys(REDIS_LATEST_KEY_PREFIX + "*");
        if (latestKeys != null) {
            redisTemplate.delete(latestKeys);
        }
    }

    @DisplayName("최근 2시간 인기 게시글을 합쳐 top10 ZSet에 저장한다")
    @Test
    void setTop10Post() {
        // given
        LocalDateTime now = LocalDateTime.now();

        String currentHourKey = REDIS_POPULAR_POSTS_KEY_PREFIX
                + now.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));

        String previousHourKey = REDIS_POPULAR_POSTS_KEY_PREFIX
                + now.minusHours(1).format(DateTimeFormatter.ofPattern("yyyyMMddHH"));

        String top10Key = REDIS_POPULAR_POSTS_KEY_PREFIX + "top10";

        redisTemplate.opsForZSet().add(currentHourKey, "1", 10);
        redisTemplate.opsForZSet().add(currentHourKey, "2", 5);
        redisTemplate.opsForZSet().add(previousHourKey, "3", 20);

        // when
        postScheduler.setTop10Post();

        // then
        Set<String> result = redisTemplate.opsForZSet()
                .reverseRange(top10Key, 0, -1);

        assertThat(result).containsExactly("3", "1", "2");
    }

    @DisplayName("Redis Hash에 저장된 조회수를 DB에 반영하고 Hash를 삭제한다")
    @Test
    void syncViewCounts() {
        // given
        User user = saveUser("user1");
        Post post = savePost(user, "제목1", PostType.FREE);

        redisTemplate.opsForHash()
                .put(REDIS_VIEW_COUNT_KEY, String.valueOf(post.getId()), "3");

        // when
        postScheduler.syncViewCounts();

        em.flush();
        em.clear();

        // then
        Post findPost = postRepository.findById(post.getId()).orElseThrow();

        assertThat(findPost.getHits()).isEqualTo(3);
        assertThat(redisTemplate.hasKey(REDIS_VIEW_COUNT_KEY)).isFalse();
    }

    @DisplayName("조회수 Hash가 비어 있으면 DB 반영 없이 종료한다")
    @Test
    void syncViewCounts_empty() {
        // given
        redisTemplate.delete(REDIS_VIEW_COUNT_KEY);

        // when
        postScheduler.syncViewCounts();

        // then
        assertThat(redisTemplate.hasKey(REDIS_VIEW_COUNT_KEY)).isFalse();
    }

    @DisplayName("게시글 타입별 최신 게시글 5개를 Redis List에 저장한다")
    @Test
    void updateLatestPosts() {
        // given
        User user = saveUser("user1");

        Post post1 = savePost(user, "제목1", PostType.FREE);
        Post post2 = savePost(user, "제목2", PostType.FREE);
        Post post3 = savePost(user, "제목3", PostType.FREE);
        Post post4 = savePost(user, "제목4", PostType.FREE);
        Post post5 = savePost(user, "제목5", PostType.FREE);
        Post post6 = savePost(user, "제목6", PostType.FREE);

        em.flush();
        em.clear();

        // when
        postScheduler.updateLatestPosts();

        // then
        String redisKey = REDIS_LATEST_KEY_PREFIX + PostType.FREE.name();

        List<String> result = redisTemplate.opsForList()
                .range(redisKey, 0, -1);

        assertThat(result).hasSize(5);
        assertThat(result)
                .containsExactly(
                        post6.getId().toString(),
                        post5.getId().toString(),
                        post4.getId().toString(),
                        post3.getId().toString(),
                        post2.getId().toString()
                );
    }

    private User saveUser(String loginId) {
        return userRepository.save(
                User.builder()
                        .loginId(loginId)
                        .password("1234")
                        .nickname(loginId)
                        .email(loginId + UUID.randomUUID() + "@test.com")
                        .userType(UserType.USER)
                        .build()
        );
    }

    private Post savePost(User user, String title, PostType postType) {
        return postRepository.save(
                Post.builder()
                        .title(title)
                        .content("내용")
                        .postType(postType)
                        .hits(0)
                        .user(user)
                        .build()
        );
    }
}
