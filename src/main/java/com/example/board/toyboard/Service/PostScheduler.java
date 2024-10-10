package com.example.board.toyboard.Service;

import com.example.board.toyboard.Config.redis.RedisKey;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;
    private final PostService postService;



    @Scheduled(fixedRate = 60000)
    public void decayPostScores() {
        Set<String> allPosts = redisTemplate.opsForZSet().reverseRange("popular-posts", 0, 100);

        if (allPosts == null) {
            return;
        }

        for (String postId : allPosts) {
            Double currentScore = redisTemplate.opsForZSet().score("popular-posts", postId);

            if (currentScore != null) {
                // 기존 점수의 90%로 줄이기
                redisTemplate.opsForZSet().add("popular-posts", postId, currentScore * 0.9);
            }
        }


        redisTemplate.opsForZSet().removeRange("popular-posts", 101, -1);
    }
    @Scheduled(fixedRate = 60000)
    public void updateHits() {
        Set<String> keys = redisTemplate.keys(RedisKey.REDIS_HITS_KEY_PREFIX + "*");

        if (keys != null) {
            for (String key : keys) {

                Long postId = Long.valueOf(key.replace(RedisKey.REDIS_HITS_KEY_PREFIX, ""));

                log.info("레디스 조회수 포스트 ID = {}", postId);

                String hitsStr = redisTemplate.opsForValue().get(key);
                if (hitsStr != null) {

                    postService.updatePostHits(postId, Integer.parseInt(hitsStr));

                    redisTemplate.delete(key);
                }
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void updateLatestPosts() {

        for (String category : makeCategoryList()) {
            String redisKey = RedisKey.REDIS_LATEST_KEY_PREFIX + category;
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
