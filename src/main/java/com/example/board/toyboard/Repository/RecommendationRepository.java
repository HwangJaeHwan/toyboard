package com.example.board.toyboard.Repository;

import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.Recommendation;
import com.example.board.toyboard.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    Optional<Recommendation> findByUserAndPost(User user, Post post);

}
