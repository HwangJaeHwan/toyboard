package com.example.board.toyboard.DTO;

import com.example.board.toyboard.Entity.Post.Post;
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

    private LocalDateTime createdTime;

    private long hits;

    private long commentNum;

    private long recommendedNumber;

    @Builder
    public PostListDTO(Long id, String title, String nickname, LocalDateTime createdTime, long hits, long commentNum, long recommendedNumber) {
        this.id = id;
        this.title = title;
        this.nickname = nickname;
        this.createdTime = createdTime;
        this.hits = hits;
        this.commentNum = commentNum;
        this.recommendedNumber = recommendedNumber;
    }

    public PostListDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.nickname = post.getUser().getNickname();
        this.createdTime = post.getCreatedTime();
        this.hits = post.getHits();
    }

    public PostListDTO(Long id, String title, String nickname, LocalDateTime createdTime, long hits) {
        this.id = id;
        this.title = title;
        this.nickname = nickname;
        this.createdTime = createdTime;
        this.hits = hits;
    }

    public PostListDTO(Long id, String title, String nickname, LocalDateTime createdTime, long hits,long recommendedNumber) {
        this.id = id;
        this.title = title;
        this.nickname = nickname;
        this.createdTime = createdTime;
        this.hits = hits;
        this.recommendedNumber = recommendedNumber;
    }
}
