package com.example.board.toyboard.Repository;

import com.example.board.toyboard.DTO.CommentReadDTO;
import com.example.board.toyboard.DTO.CommentReportDTO;
import com.example.board.toyboard.Entity.Post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom {


    Page<CommentReportDTO> commentReports(Pageable pageable);

    List<CommentReadDTO> commentInfo(Post post);

}
