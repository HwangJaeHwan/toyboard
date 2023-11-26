package com.example.board.toyboard.Repository;

import com.example.board.toyboard.DTO.PageListDTO;
import com.example.board.toyboard.DTO.PostListDTO;
import com.example.board.toyboard.DTO.PostReportDTO;
import com.example.board.toyboard.DTO.SearchDTO;
import com.example.board.toyboard.Entity.Post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {


    Page<PostListDTO> search(SearchDTO searchDTO, Pageable pageable, String postType);

    Page<PostReportDTO> reportedList(Pageable pageable);


}
