package com.example.board.toyboard.DTO;

import com.example.board.toyboard.Entity.User;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserEditDTO {


    @NotEmpty(message = "닉네임을 입력해주세요")
    private String nickname;

    public UserEditDTO(String nickname) {
        this.nickname = nickname;
    }
}
