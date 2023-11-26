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
@Builder
public class PostReadDTO {

    private Long id;

    private String nickname;

    private String title;

    private String content;

    private LocalDateTime createTime;

    private int hits;

    private int recommendedNumber;

    public PostReadDTO(Post post) {
        this.id = post.getId();
        this.nickname = post.getUser().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createTime = post.getCreatedTime();
        this.hits = post.getHits();
    }
}
