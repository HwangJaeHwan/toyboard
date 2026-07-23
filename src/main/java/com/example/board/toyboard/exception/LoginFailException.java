package com.example.board.toyboard.exception;

public class LoginFailException extends ToyboardException {

    public static String MESSAGE = "아이디 또는 비밀번호가 일치하지 않습니다.";

    public LoginFailException() {
        super(MESSAGE);
    }

    public LoginFailException(String message) {
        super(message);
    }

    @Override
    public String getStatusCode() {
        return null;
    }
}