package com.example.board.toyboard.Entity.Post;


import com.example.board.toyboard.DTO.PostWriteDTO;
import com.example.board.toyboard.Entity.BaseEntity;
import com.example.board.toyboard.Entity.Report.CommentReport;
import com.example.board.toyboard.Entity.Report.PostReport;
import com.example.board.toyboard.Entity.User;
import lombok.*;

import jakarta.persistence.*;

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

    private PostType postType;

    private long hits;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostReport> reports = new ArrayList<>();



    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder

    public Post(String title, String content, PostType postType, int hits, User user) {
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

    public void updatePostType(PostType postType){
        this.postType = postType;
    }


    public void assignWriter(User user) {
        this.user = user;
        user.addPost(this);
    }


    public void addHits() {
        hits++;
    }

    public void addReport(User user) {
        PostReport report = new PostReport(user, this);
        reports.add(report);
    }

    public void removeReport(PostReport report) {
        reports.remove(report);
    }

    public static Post create(PostWriteDTO dto, User user) {
        Post post = new Post();
        post.title = dto.getTitle();
        post.content = dto.getContent();
        post.postType = dto.getPostType();
        post.hits = 0;

        post.assignWriter(user);

        return post;
    }





}
