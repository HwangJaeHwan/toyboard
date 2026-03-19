package com.example.board.toyboard.Repository.post;

import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

//    @Query("select p from Post p join fetch p.user where p.id = :id")
//    Optional<Post> findByIdWithUser(@Param("id") Long id);

    @Query("select p from Post p where p.id in :ids")
    List<Post> findPopularPosts(@Param("ids") List<Long> ids);

    @Query("select p from Post p join fetch p.user where p.postType = :postType order by p.createdTime DESC")
    Page<Post> findPostList(PostType postType, Pageable pageable);

    List<Post> findTop5PostByPostTypeOrderByCreatedTimeDesc(PostType postType);




}
