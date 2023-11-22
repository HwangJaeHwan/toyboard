package com.example.board.toyboard.Repository;

import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long>,CommentRepositoryCustom {

    @Query("select c from Comment c join fetch c.user where c.post = :post")
    List<Comment> findCommentsByPost(@Param("post") Post post);

    @Query("select c from Comment c join fetch c.post join fetch c.user where c.id = :id")
    Optional<Comment> findWithPostAndUser(@Param("id") Long id);

    void deleteAllByPost(Post post);


}
