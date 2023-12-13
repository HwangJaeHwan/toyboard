package com.example.board.toyboard.DTO;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class RegisterDTO {

    @NotEmpty(message = "아이디를 입력해주세요")
    @Pattern(regexp="^(?=.*[a-zA-Z0-9]).{5,20}$"
            ,message = "5~20자의 영문, 숫자만 사용 가능합니다.")
    private String loginId;
    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}"
            ,message = "숫자, 영어, 특수문자 포함 8~20자입니다.")
    private String password;
    @NotEmpty(message = "비밀번호 확인을 입력해주세요")
    private String passwordCheck;
    @NotEmpty(message = "닉네임을 입력해주세요")
    private String nickname;
    @NotEmpty(message = "이메일을 입력해주세요")
    private String email;
}
