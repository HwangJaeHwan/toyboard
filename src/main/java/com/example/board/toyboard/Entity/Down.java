package com.example.board.toyboard.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
public class Down extends BaseEntity{



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "down_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
    @Builder
    public Down(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }
}
