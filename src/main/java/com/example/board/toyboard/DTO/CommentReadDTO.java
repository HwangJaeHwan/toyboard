package com.example.board.toyboard.DTO;

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

    Long userId;

    String nickname;

    String content;

    Long up;

    Long down;

    Long report;

    Long reply;

}
