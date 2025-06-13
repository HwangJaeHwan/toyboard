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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

        }


    }


    public List<HomePost> getPopularPost() {

        Set<String> set = redisTemplate.opsForZSet().reverseRange(REDIS_POPULAR_POSTS_KEY_PREFIX, 0, 10);


        if (set == null) {
            return null;
        }


        List<Long> ids = set.stream().map(Long::valueOf).toList();
        List<Post> popularPosts = postRepository.findPopularPosts(ids);
        popularPosts.sort(Comparator.comparing(post -> ids.indexOf(post.getId())));

        return popularPosts.stream().map(HomePost::new).toList();

    }
    public void incrementPostView(Long postId) {

        redisTemplate.opsForZSet().incrementScore(REDIS_POPULAR_POSTS_KEY_PREFIX, String.valueOf(postId), 1);
    }


}
