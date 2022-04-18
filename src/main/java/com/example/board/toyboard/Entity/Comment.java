package com.example.board.toyboard.Entity;


import com.example.board.toyboard.DTO.CommentReadDTO;
import com.example.board.toyboard.DTO.CommentWriteDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String comment;


    private int up;

    private int down;

    private int report;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;


    public void setPost(Post post) {
        this.post = post;
        post.addComment(this);
    }


    public CommentReadDTO makeReadDTO(String nickname) {

        return CommentReadDTO.builder()
                .id(id)
                .nickname(nickname)
                .content(comment)
                .up(up)
                .down(down)
                .report(report)
                .build();

    }


    public void addUp() {
        up += 1;
    }

    public void subUp(){
        up -= 1;
    }

    public void addDown() {
        down += 1;
    }

    public void subDown(){
        down -= 1;
    }

    public void commentReport() {
        report++;
    }





}
