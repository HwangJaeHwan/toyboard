package com.example.board.toyboard.session;

import com.example.board.toyboard.Entity.UserType;
import lombok.Getter;

@Getter

public class UserSession {

    private Long userId;
    private String nickname;
    private UserType userType;

    public UserSession(Long userId, String nickname, UserType userType) {

        this.userId = userId;
        this.nickname = nickname;
        this.userType = userType;
    }
}
