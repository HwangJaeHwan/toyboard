package com.example.board.toyboard.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentReportDTO {

    private Long postId;

    private String postTitle;

    private String comment;

    private Long userId;

    private String nickname;

    private Long reposts;

    public CommentReportDTO(Long postId, String postTitle, String comment, Long userId, String nickname, Long reposts) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.comment = comment;
        this.userId = userId;
        this.nickname = nickname;
        this.reposts = reposts;
    }
}
