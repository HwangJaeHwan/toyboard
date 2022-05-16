package com.example.board.toyboard.Repository;

import com.example.board.toyboard.DTO.SearchDTO;
import com.example.board.toyboard.Entity.Post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {


    Page<Post> search(SearchDTO searchDTO, Pageable pageable);

}
