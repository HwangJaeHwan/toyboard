package com.example.board.toyboard.repository;

import com.example.board.toyboard.DTO.CommentReportDTO;
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
