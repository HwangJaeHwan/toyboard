package com.example.board.toyboard.Service;

import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.example.board.toyboard.Config.redis.RedisKey.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;
    private final PostService postService;



    @Scheduled(fixedRate = 60000)
    public void decayPostScores() {
        Set<String> allPosts = redisTemplate.opsForZSet().reverseRange(REDIS_POPULAR_POSTS_KEY_PREFIX, 0, 99);

        if (allPosts == null) {
            return;
        }

        for (String postId : allPosts) {
            Double currentScore = redisTemplate.opsForZSet().score(REDIS_POPULAR_POSTS_KEY_PREFIX, postId);

            if (currentScore != null) {
                // 기존 점수의 90%로 줄이기
                redisTemplate.opsForZSet().add(REDIS_POPULAR_POSTS_KEY_PREFIX, postId, currentScore * 0.9);
            }
        }


        redisTemplate.opsForZSet().removeRange(REDIS_POPULAR_POSTS_KEY_PREFIX, 101, -1);
    }
    @Scheduled(fixedDelay = 60000)
    public void updateViewCounts() {
        Set<String> keys = redisTemplate.keys(REDIS_VIEW_COUNT_KEY_PREFIX + "*");


        if (keys == null || keys.isEmpty()) {
            return;
        }


        for (String key : keys) {

            String viewCountsString = redisTemplate.opsForValue().get(key);
            redisTemplate.delete(key);

            if (viewCountsString == null) {
                continue;
            }

            Long postId = Long.valueOf(key.substring(REDIS_VIEW_COUNT_KEY_PREFIX.length()));
            int viewCount = Integer.parseInt(viewCountsString);
            postService.updatePostViewCounts(postId, viewCount);
        }

    }

    @Scheduled(fixedRate = 60000)
    public void updateLatestPosts() {

        for (String category : makeCategoryList()) {
            String redisKey = REDIS_LATEST_KEY_PREFIX + category;
            redisTemplate.delete(redisKey);

            List<Post> posts = postRepository.findTop5PostByPostTypeOrderByCreatedTimeDesc(category);

            for (Post post : posts) {
                String postString = post.getId() + ":" + post.getTitle();
                redisTemplate.opsForList().rightPush(redisKey, postString);
            }

        }





    }

    private List<String> makeCategoryList() {

        return List.of("FREE", "NOTICE", "INFO", "QNA");
    }



}
