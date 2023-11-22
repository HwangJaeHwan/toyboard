package com.example.board.toyboard.DTO;

import com.example.board.toyboard.Entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReadDTO {


    Long id;

    String nickname;

    String content;

    int up;

    int down;

    Long report;

    public CommentReadDTO(Comment comment) {
        this.id = comment.getId();
        this.nickname = comment.getUser().getNickname();
        this.content = comment.getComment();
        this.up = comment.getUps().size();
        this.down = comment.getDowns().size();
        this.report = 1L;
    }
}
