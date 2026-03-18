package com.example.board.toyboard.Exception;

public class PasswordMismatchException extends RuntimeException {

    public static String MESSAGE = "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.";

    public PasswordMismatchException() {
        super(MESSAGE);
    }
}
