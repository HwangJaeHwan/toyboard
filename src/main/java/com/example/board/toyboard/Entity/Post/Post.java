package com.example.board.toyboard.Entity.Post;


import com.example.board.toyboard.Entity.BaseEntity;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.User;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;


    @Lob
    private String content;

    private String postType;

    private int hits;



    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Post(String title, String content, String postType, int hits, User user) {

        this.title = title;
        this.content = content;
        this.postType = postType;
        this.hits = hits;
        this.user = user;
    }

    public void updateTitle(String title){
        this.title = title;
    }
    public void updateContent(String content) {
        this.content = content;
    }

    public void updatePostType(String postType){
        this.postType = postType;
    }


    public void setWriter(User user) {
        this.user = user;
        user.addPost(this);
    }


    public void addHits() {
        hits++;
    }




}
