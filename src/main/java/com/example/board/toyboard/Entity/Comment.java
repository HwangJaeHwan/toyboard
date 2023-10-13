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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
@ToString
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
    @Builder.Default
    private List<Up> ups = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Down> downs = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Log> commentLogs = new ArrayList<>();


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
