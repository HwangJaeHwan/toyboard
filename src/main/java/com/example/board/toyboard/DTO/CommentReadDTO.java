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

    String nickname;

    String content;

    int up;

    int down;

}
