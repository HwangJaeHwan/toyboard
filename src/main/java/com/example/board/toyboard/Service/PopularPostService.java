package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.HomePost;
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


    public List<HomePost> getPopularPost() {

        Set<String> set = redisTemplate.opsForZSet().reverseRange("popular-posts", 0, 10);


        if (set == null) {
            return null;
        }


        List<Long> ids = set.stream().map(Long::valueOf).toList();
        List<Post> popularPosts = postRepository.findPopularPosts(ids);
        popularPosts.sort(Comparator.comparing(post -> ids.indexOf(post.getId())));

        return popularPosts.stream().map(HomePost::new).toList();

    }
    public void incrementPostView(Long postId) {

        redisTemplate.opsForZSet().incrementScore("popular-posts", String.valueOf(postId), 1);
    }


}
