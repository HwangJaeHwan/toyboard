package com.example.board.toyboard.Repository;

import com.example.board.toyboard.DTO.CommentReadDTO;
import com.example.board.toyboard.DTO.CommentReportDTO;
import com.example.board.toyboard.Entity.Post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CommentRepositoryCustom {


    Page<CommentReportDTO> commentReports(Pageable pageable);
    Map<Long, Long> getUpCounts(List<Long> commentIds);
    Map<Long, Long> getDownCounts(List<Long> commentIds);
    Map<Long, Long> getReportCounts(List<Long> commentIds);

    Map<Long, Long> getReplyCounts(List<Long> commentIds);

}
