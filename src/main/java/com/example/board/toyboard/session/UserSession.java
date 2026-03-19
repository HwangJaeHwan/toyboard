package com.example.board.toyboard.session;

import com.example.board.toyboard.Entity.UserType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSession {

    private Long id;
    private String nickname;
    private UserType userType;

    @Builder
    public UserSession(Long id, String nickname, UserType userType) {
        this.id = id;
        this.nickname = nickname;
        this.userType = userType;
    }
}
