package com.example.board.toyboard.DTO;

import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class SearchInfo {

    private Long id;
    private LocalDateTime createAt;
    private Long hits;

    public SearchInfo(Long id, LocalDateTime createAt, Long hits, Long recommendCount) {
        this.id = id;
        this.createAt = createAt;
        this.hits = hits;
        this.recommendCount = recommendCount;
    }

    private Long recommendCount;




}
