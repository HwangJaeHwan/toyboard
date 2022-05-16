package com.example.board.toyboard.Entity.Post;


import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Post {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;


    @Column(length = 1000)
    private String content;

    private LocalDateTime createTime;


    private String postType;

    private int hits;

    private int recommendedNumber;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();


    public void updateTitle(String title){
        this.title = title;
    }
    public void updateContent(String content) {
        this.content = content;
    }

    public void updatePostType(String postType){
        this.postType = postType;
    }


    public void setUser(User user) {
        this.user = user;
        user.addPost(this);
    }


    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void addHits() {
        hits++;
    }



}