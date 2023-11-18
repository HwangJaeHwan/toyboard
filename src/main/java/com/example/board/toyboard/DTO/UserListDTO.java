package com.example.board.toyboard.DTO;

import com.example.board.toyboard.Entity.User;

public class UserListDTO {


    private final String loginId;

    private final String nickname;

    private final String email;


    public UserListDTO(User user) {
        this.loginId = user.getLoginId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();

    }
}
