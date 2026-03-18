package com.example.board.toyboard.Service;

import com.example.board.toyboard.Config.redis.RedisKey;
import com.example.board.toyboard.DTO.HomePost;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import static com.example.board.toyboard.Config.redis.RedisKey.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;


    public void test() {
        redisTemplate.delete("popular-posts");
    }


    public void incrementViewCount(Long postId, String nickname) {

        String postKey = REDIS_POST_VIEWERS_KEY_PREFIX + postId;
        Long check = redisTemplate.opsForSet().add(postKey, nickname);

        if (Objects.equals(check, 1L)) {

            String viewCountKey = REDIS_VIEW_COUNT_KEY_PREFIX + postId;
            redisTemplate.opsForValue().increment(viewCountKey);
            redisTemplate.expire(postKey, Duration.ofDays(1));
            recordPopularPostView(postId);

        }


    }


    public List<HomePost> getPopularPost() {

        List<String> keys = IntStream.range(0, 2).mapToObj(i ->
                        REDIS_POST_VIEWERS_KEY_PREFIX + LocalDateTime.now().minusHours(i)
                                .format(DateTimeFormatter.ofPattern("yyyyMMddHH")))
                .toList();

        String top10Key = REDIS_POPULAR_POSTS_KEY_PREFIX + "top10";

        redisTemplate.opsForZSet().unionAndStore(keys.get(0), keys.subList(1, keys.size()), top10Key);


        Set<String> set = redisTemplate.opsForZSet().reverseRange(top10Key, 0, 9);

        if (set == null || set.isEmpty()) {
            return null;
        }


        List<Long> ids = set.stream().map(Long::valueOf).toList();
        List<Post> popularPosts = postRepository.findPopularPosts(ids);

        return popularPosts.stream().map(HomePost::new).toList();

    }
    public void incrementPostView(Long postId) {

        redisTemplate.opsForZSet().incrementScore(REDIS_POPULAR_POSTS_KEY_PREFIX, String.valueOf(postId), 1);
    }

    private void recordPopularPostView(Long postId) {

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        String key = REDIS_POPULAR_POSTS_KEY_PREFIX + now;

        redisTemplate.opsForZSet().incrementScore(key, String.valueOf(postId), 1);
    }


}
