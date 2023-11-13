package com.example.board.toyboard.Entity.Report;

import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import lombok.*;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostReport extends Report{

    public PostReport(User user, Post post) {
        super(user);
        this.post = post;
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

}
