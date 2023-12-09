package com.example.board.toyboard.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentReportDTO {

    private Long id;

    private Long postId;

    private String postTitle;

    private String comment;

    private String nickname;

    private Long reposts;

    public CommentReportDTO(Long id, Long postId, String postTitle, String comment, String nickname, Long reposts) {
        this.id = id;
        this.postId = postId;
        this.postTitle = postTitle;
        this.comment = comment;
        this.nickname = nickname;
        this.reposts = reposts;
    }
}
