package com.example.board.toyboard.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostReadDTO {

    private Long id;

    private String nickname;

    private String title;

    private String content;

    private LocalDateTime createTime;

    private int hits;

    private int recommendedNumber;


}
