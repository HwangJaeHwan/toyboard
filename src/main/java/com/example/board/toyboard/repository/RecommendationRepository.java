package com.example.board.toyboard.repository;

import com.example.board.toyboard.entity.post.Post;
import com.example.board.toyboard.entity.post.Recommendation;
import com.example.board.toyboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    Optional<Recommendation> findByUserAndPost(User user, Post post);

    @Modifying
    @Query("delete from Recommendation r where r.post = :post")
    void deleteAllByPost(@Param("post")Post post);

    int countAllByPostId(Long postId);

}
