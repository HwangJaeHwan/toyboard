package com.example.board.toyboard.DTO;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class UserEditDTO {


    @NotEmpty(message = "닉네임을 입력해주세요")
    private String nickname;

    public UserEditDTO(String nickname) {
        this.nickname = nickname;
    }
}
