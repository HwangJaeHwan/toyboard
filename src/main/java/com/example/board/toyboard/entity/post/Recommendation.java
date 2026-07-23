package com.example.board.toyboard.entity.post;

import com.example.board.toyboard.entity.BaseEntity;
import com.example.board.toyboard.entity.User;
import lombok.*;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;;
@Entity
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommendation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public Recommendation(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
