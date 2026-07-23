package com.example.board.toyboard.exception;

public class InvalidPasswordException extends ToyboardException {

    public static String MESSAGE = "현재 비밀번호가 틀립니다.";

    public InvalidPasswordException() {
        super(MESSAGE);
    }

    @Override
    public String getStatusCode() {
        return null;
    }
}
