package com.example.board.toyboard.Repository;

import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long>,CommentRepositoryCustom {


    @Query("select c from Comment c join fetch c.user where c.id = :id")
    Optional<Comment> findByIdWithUser(@Param("id") Long id);


    @Query("select c from Comment c join fetch c.user where c.post.id = :postId and c.parent is null")
    List<Comment> findCommentsByPost(@Param("postId") Long postId);

    @Query("select c from Comment c join fetch c.post join fetch c.user where c.id = :id")
    Optional<Comment> findWithPostAndUser(@Param("id") Long id);

    @Query("select c from Comment c join fetch c.ups where c.id = :id")
    Optional<Comment> findCommentWithUps(@Param("id") Long id);

    @Query("select c from Comment c join fetch c.downs where c.id = :id")
    Optional<Comment> findCommentWithDowns(@Param("id") Long id);


    void deleteAllByPost(Post post);

    @Query("select r from Comment r join fetch r.user where r.parent.id = :commentId")
    List<Comment> findRepliesByCommentId(Long commentId);



}
