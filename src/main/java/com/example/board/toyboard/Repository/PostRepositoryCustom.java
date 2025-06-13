package com.example.board.toyboard.Repository;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.Post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostRepositoryCustom {

    LatestPosts getLatestPosts();
    Page<PostListDTO> search(SearchDTO searchDTO, Pageable pageable, String postType);

    Page<PostReportDTO> reportedList(Pageable pageable);

    Optional<PostReadDTO> postRead(Long postId);


}
