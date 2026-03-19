package com.example.board.toyboard.Service;

import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.PostType;
import com.example.board.toyboard.Repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.example.board.toyboard.Config.redis.RedisKey.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;



    @Scheduled(fixedRate = 600000)
    public void setTop10Post() {

        LocalDateTime now = LocalDateTime.now();

        List<String> keys = IntStream.range(0,2)
                .mapToObj(i ->
                        REDIS_POPULAR_POSTS_KEY_PREFIX +
                                now.minusHours(i)
                                        .format(DateTimeFormatter.ofPattern("yyyyMMddHH")))
                .toList();

        String top10Key = REDIS_POPULAR_POSTS_KEY_PREFIX + "top10";

        redisTemplate.opsForZSet()
                .unionAndStore(keys.get(0), keys.subList(1, keys.size()), top10Key);
    }



    @Scheduled(fixedRate = 60000)
    public void syncViewCounts() {

        Map<Object, Object> viewCounts =
                redisTemplate.opsForHash().entries(REDIS_VIEW_COUNT_KEY);

        if (viewCounts.isEmpty()) {
            return;
        }

        Map<Long, Integer> viewCountMap = new HashMap<>();

        for (Map.Entry<Object, Object> entry : viewCounts.entrySet()) {

            long postId = Long.parseLong(entry.getKey().toString());
            int count = Integer.parseInt(entry.getValue().toString());

            viewCountMap.put(postId, count);

        }

        postRepository.bulkUpdateViewCounts(viewCountMap);


        redisTemplate.delete(REDIS_VIEW_COUNT_KEY);
    }






    @Scheduled(fixedRate = 60000)
    public void updateLatestPosts() {

        for (PostType postType : PostType.values()) {
            String redisKey = REDIS_LATEST_KEY_PREFIX + postType.name();
            redisTemplate.delete(redisKey);

            List<Post> posts = postRepository.findTop5PostByPostTypeOrderByCreatedTimeDesc(postType);

            for (Post post : posts) {
                String postString = post.getId() + ":" + post.getTitle();
                redisTemplate.opsForList().rightPush(redisKey, postString);
            }

        }





    }


}
