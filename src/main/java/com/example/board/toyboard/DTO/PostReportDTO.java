package com.example.board.toyboard.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostReportDTO {

    private Long id;

    private String title;

    private String userLoginId;

    private String nickname;

    private Long reposts;

    public PostReportDTO(Long id, String title, String userLoginId, String nickname, Long reposts) {
        this.id = id;
        this.title = title;
        this.userLoginId = userLoginId;
        this.nickname = nickname;
        this.reposts = reposts;
    }
}
