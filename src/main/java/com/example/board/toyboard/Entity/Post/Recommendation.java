package com.example.board.toyboard.Entity.Post;

import com.example.board.toyboard.Entity.BaseEntity;
import com.example.board.toyboard.Entity.User;
import lombok.*;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommendation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    private User user;

    @ManyToOne(fetch = LAZY)
    private Post post;

    public Recommendation(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
