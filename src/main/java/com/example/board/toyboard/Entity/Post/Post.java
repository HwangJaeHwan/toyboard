package com.example.board.toyboard.Entity.Post;


import com.example.board.toyboard.Entity.BaseEntity;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "POST_SEQ_GENERATOR",
        sequenceName = "POST_SEQ")
public class Post extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "post_id")
    private Long id;

    private String title;


    @Column(length = 1000)
    private String content;

    private String postType;

    private int hits;

    private int recommendedNumber;

    private int commentNum;


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;


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

    public void addCommentNum(){
        this.commentNum++;
    }

    public void subCommentNum(){
        this.commentNum--;
    }

    public void addRecommendedNumber(){
        recommendedNumber++;
    }

    public void subRecommendedNumber(){
        recommendedNumber--;
    }



}
