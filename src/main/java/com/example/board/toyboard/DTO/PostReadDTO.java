package com.example.board.toyboard.DTO;

import com.example.board.toyboard.Entity.Post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostReadDTO {

    private Long id;

    private String nickname;

    private String title;

    private String content;

    private LocalDateTime createTime;

    private int hits;

    private long recommendedNumber;


}
