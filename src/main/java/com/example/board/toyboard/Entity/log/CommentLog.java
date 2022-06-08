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


    public CommentLog(User user, Post post, LogType type, Comment comment) {
        super(user, post, type);
        this.comment = comment;
    }


    @ManyToOne(fetch = LAZY)
    @JoinColumn
    private Comment comment;
}
