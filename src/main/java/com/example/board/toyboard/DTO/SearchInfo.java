package com.example.board.toyboard.DTO;

import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class SearchInfo {

    private Long id;
    private LocalDateTime createAt;
    private Long viewCount;

    public SearchInfo(Long id, LocalDateTime createAt, Long viewCount, Long recommendCount) {
        this.id = id;
        this.createAt = createAt;
        this.viewCount = viewCount;
        this.recommendCount = recommendCount;
    }

    private Long recommendCount;




}
