package com.example.board.toyboard.service;

import com.example.board.toyboard.DTO.HomePost;
import com.example.board.toyboard.entity.post.Post;
import com.example.board.toyboard.entity.post.PostType;
import com.example.board.toyboard.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.board.toyboard.config.redis.RedisKey.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;



    public void incrementViewCount(Long postId, Long userId) {

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String postKey = REDIS_POST_VIEWERS_KEY_PREFIX + postId + ":" + date;
        Long check = redisTemplate.opsForSet().add(postKey, userId.toString());

        log.info("userId={}", userId);
        log.info("postKey={}", postKey);
        log.info("check={}", check);


        if (check != null && check == 1L) {

            redisTemplate.opsForHash()
                    .increment(REDIS_VIEW_COUNT_KEY, String.valueOf(postId), 1);
            redisTemplate.expire(postKey, Duration.ofHours(3));
            recordPopularPostView(postId);

        }


    }



    public void saveLatestPost(PostType postType, Long postId) {

        String redisKey = REDIS_LATEST_KEY_PREFIX + postType.name();

        redisTemplate.opsForList().leftPush(redisKey, postId.toString());
        redisTemplate.opsForList().trim(redisKey, 0, 4);


    }



    public List<HomePost> getPopularPost() {

        String top10Key = REDIS_POPULAR_POSTS_KEY_PREFIX + "top10";

        Set<String> ids =
                redisTemplate.opsForZSet()
                        .reverseRange(top10Key,0,9);

        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> postIds =
                ids.stream().map(Long::valueOf).toList();

        List<Post> posts =
                postRepository.findPopularPosts(postIds);

        sortPosts(postIds, posts);


        return posts.stream()
                .map(HomePost::new)
                .toList();
    }

    private void sortPosts(List<Long> postIds, List<Post> posts) {
        Map<Long, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < postIds.size(); i++) {
            orderMap.put(postIds.get(i), i);
        }

        posts.sort(Comparator.comparingInt(post -> orderMap.get(post.getId())));
    }

//    public void incrementPostView(Long postId) {
//
//        redisTemplate.opsForZSet().incrementScore(REDIS_POPULAR_POSTS_KEY_PREFIX, String.valueOf(postId), 1);
//    }


    private void recordPopularPostView(Long postId) {

        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHH"));

        String key = REDIS_POPULAR_POSTS_KEY_PREFIX + date;

        redisTemplate.opsForZSet()
                .incrementScore(key, postId.toString(), 1);

        redisTemplate.expire(key, Duration.ofHours(3));
    }


}
