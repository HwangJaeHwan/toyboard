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
public class PostListDTO {


    private Long id;

    private String title;

    private String nickname;

    private LocalDateTime createTime;

    private int hits;

    private int commentNum;

    private int recommendedNumber;



    public PostListDTO(Post post) {
        this.id = post.getId();
        this.nickname = post.getUser().getNickname();
        this.title = post.getTitle();
        this.createTime = post.getCreatedTime();
        this.hits = post.getHits();
        this.commentNum = post.getCommentNum();
        this.recommendedNumber = post.getRecommendedNumber();
    }




}
