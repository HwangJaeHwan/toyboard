package com.example.board.toyboard.exception;

public class PasswordMismatchException extends ToyboardException {

    public static String MESSAGE = "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.";

    public PasswordMismatchException() {
        super(MESSAGE);
    }

    @Override
    public String getStatusCode() {
        return null;
    }
}
