package com.example.board.toyboard.Entity.log;

import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLog extends Log {


    public CommentLog(User user, Post post, String message, User commentUser, Comment comment) {
        super(user, post, message);
        this.user = commentUser;
        this.comment = comment;
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    @Column(name = "comment_user")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn
    private Comment comment;
}
