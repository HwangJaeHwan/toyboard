package com.example.board.toyboard.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostReportDTO {

    private Long postId;

    private String title;

    private Long userId;

    private String nickname;

    private Long reposts;

    public PostReportDTO(Long postId, String title, Long userId, String nickname, Long reposts) {
        this.postId = postId;
        this.title = title;
        this.userId = userId;
        this.nickname = nickname;
        this.reposts = reposts;
    }
}
