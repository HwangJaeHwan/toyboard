package com.example.board.toyboard.Entity;


import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.log.Log;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private final List<Up> ups = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private final List<Down> downs = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private final List<Log> commentLogs = new ArrayList<>();


    @Builder
    public Comment(String comment, int up, int down, int report, User user, Post post) {
        this.comment = comment;
        this.up = up;
        this.down = down;
        this.report = report;
        this.user = user;
        this.post = post;
    }

    public void addUp(Up up) {
        ups.add(up);

        this.up += 1;
    }

    public void subUp(Up up) {
        ups.remove(up);
        this.up -= 1;
    }

    public void addDown(Down down) {
        downs.add(down);
        this.down += 1;
    }

    public void subDown(Down down){
        downs.remove(down);
        this.down -= 1;
    }

    public void commentReport() {
        report++;
    }


    public void addLog(Log commentLog) {
        commentLogs.add(commentLog);
    }

    public void removeLog(Log commentLog) {
        commentLogs.remove(commentLog);
    }
}
