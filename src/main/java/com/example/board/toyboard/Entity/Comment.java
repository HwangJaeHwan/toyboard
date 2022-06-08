package com.example.board.toyboard.Entity;


import com.example.board.toyboard.DTO.CommentReadDTO;
import com.example.board.toyboard.Entity.Post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class Comment extends BaseEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String comment;


    private int up;

    private int down;

    private int report;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private List<Up> ups = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private List<Down> downs = new ArrayList<>();


    public void addUp(Up up) {
        ups.add(up);
        this.up += 1;
    }

    public void subUp(Up up) {

        for (Up up1 : ups) {
            log.info("up1 = {}", up1);
        }
        ups.remove(up);
        for (Up up1 : ups) {
            log.info("up2 = {}", up1);
        }
        this.up -= 1;
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
