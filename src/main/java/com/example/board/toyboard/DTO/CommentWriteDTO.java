package com.example.board.toyboard.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CommentWriteDTO {


    Long userId;

    Long postId;

    String comment;


}
