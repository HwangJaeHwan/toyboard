package com.example.board.toyboard.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostListDTO {


    private Long id;

    private String title;

    private String nickname;

    private LocalDateTime createTime;

    private int hits;

    private int recommendedNumber;

}
