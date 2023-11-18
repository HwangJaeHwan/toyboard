package com.example.board.toyboard.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostReportDTO {

    private Long id;

    private String title;

    private String nickname;

    private int reposts;

    public PostReportDTO(Long id, String title, String nickname, int reposts) {
        this.id = id;
        this.title = title;
        this.nickname = nickname;
        this.reposts = reposts;
    }
}
