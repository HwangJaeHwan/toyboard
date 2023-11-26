package com.example.board.toyboard.DTO;

import com.example.board.toyboard.Entity.Post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostListDTO {


    private Long id;

    private String title;

    private String nickname;

    private LocalDateTime createTime;

    private int hits;

    private long commentNum;

    private long recommendedNumber;

    @Builder
    public PostListDTO(Long id, String title, String nickname, LocalDateTime createTime, int hits, long commentNum, long recommendedNumber) {
        this.id = id;
        this.title = title;
        this.nickname = nickname;
        this.createTime = createTime;
        this.hits = hits;
        this.commentNum = commentNum;
        this.recommendedNumber = recommendedNumber;
    }
}
