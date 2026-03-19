package com.example.board.toyboard.Repository.post;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PostRepositoryCustom {

    List<PostTitle> findLatestPostsByType(PostType postType, int limit);

    LatestPosts getLatestPosts();
    Page<PostListDTO> list(Pageable pageable, PostType postType);

    Page<PostReportDTO> reportedList(Pageable pageable);

    PostReadDTO postRead(Long postId);

    Map<Long, SearchInfo> search(List<Long> ids);

    PostReadDTO postReadWithHighlight(Long postId, String keyword);

    Page<PostListDTO> findPosts(Pageable pageable, PostType postType);

    Page<PostListDTO> findPostsOrderByRecommendation(Pageable pageable, PostType postType);

    Map<Long, Long> countRecommendationsByPostIds(List<Long> ids);

    Map<Long, Long> countCommentsByPostIds(List<Long> ids);

    void bulkUpdateViewCounts(Map<Long, Integer> viewCountMap);



}
