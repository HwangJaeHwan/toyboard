package com.example.board.toyboard.DTO;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class PasswordChangeDTO {

    @NotEmpty( message = "현재 비밀번호를 입력해주세요")
    private String nowPassword;

    @NotEmpty(message = "새 비밀번호를 입력해주세요")
    private String newPassword;

    @NotEmpty(message = "새 비밀번호 확인을 입력해주세요")
    private String newPasswordCheck;


}
