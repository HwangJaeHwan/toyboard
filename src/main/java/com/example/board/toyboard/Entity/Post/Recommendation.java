package com.example.board.toyboard.Entity.Post;

import com.example.board.toyboard.Entity.BaseEntity;
import com.example.board.toyboard.Entity.User;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Recommendation extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "recommendation_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    private User user;

    @ManyToOne(fetch = LAZY)
    private Post post;


}
