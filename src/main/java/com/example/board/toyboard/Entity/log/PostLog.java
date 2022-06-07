package com.example.board.toyboard.Entity.log;

import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLog extends Log {

    public PostLog(User user, Post post, String message) {
        super(user, post, message);
    }
}
