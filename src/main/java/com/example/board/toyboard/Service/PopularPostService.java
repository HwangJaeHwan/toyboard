package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.PostTitle;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PopularPostService {

    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;

    public void test() {
        redisTemplate.delete("popular-posts");
    }


    public List<PostTitle> getPopularPost() {

        Set<String> set = redisTemplate.opsForZSet().reverseRange("popular-posts", 0, 10);


        if (set == null) {
            return null;
        }


        List<Long> ids = set.stream().map(Long::valueOf).toList();
        List<Post> popularPosts = postRepository.findPopularPosts(ids);
        popularPosts.sort(Comparator.comparing(post -> ids.indexOf(post.getId())));

        return popularPosts.stream().map(PostTitle::new).toList();

    }
    public void incrementPostView(Long postId) {

        redisTemplate.opsForZSet().incrementScore("popular-posts", String.valueOf(postId), 1);
    }

    @Scheduled(fixedRate = 60000) // 10분마다 실행
    public void decayPostScores() {
        Set<String> allPosts = redisTemplate.opsForZSet().reverseRange("popular-posts", 0, 100);

        for (String postId : allPosts) {
            Double currentScore = redisTemplate.opsForZSet().score("popular-posts", postId);

            if (currentScore != null) {
                // 기존 점수의 90%로 줄이기
                redisTemplate.opsForZSet().add("popular-posts", postId, currentScore * 0.9);
            }
        }

        // 상위 100개를 제외한 나머지 삭제 (101번째부터 끝까지 삭제)
        redisTemplate.opsForZSet().removeRange("popular-posts", 101, -1);
    }

}
