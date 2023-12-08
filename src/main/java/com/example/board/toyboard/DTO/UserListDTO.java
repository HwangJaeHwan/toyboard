package com.example.board.toyboard.DTO;

import com.example.board.toyboard.Entity.User;
import lombok.Getter;

@Getter
public class UserListDTO {

    private final Long id;

    private final String loginId;

    private final String nickname;

    private final String email;


    public UserListDTO(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
    }
}
