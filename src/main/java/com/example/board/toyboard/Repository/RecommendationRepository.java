package com.example.board.toyboard.Repository;

import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.Recommendation;
import com.example.board.toyboard.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    Optional<Recommendation> findByUserAndPost(User user, Post post);

    @Modifying
    @Query("delete from Recommendation r where r.post = :post")
    void deleteAllByPost(Post post);

    int countAllByPostId(Long postId);

}
