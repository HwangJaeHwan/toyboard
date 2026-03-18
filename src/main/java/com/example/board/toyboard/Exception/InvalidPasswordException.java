package com.example.board.toyboard.Exception;

public class InvalidPasswordException extends RuntimeException {

    public static String MESSAGE = "현재 비밀번호가 틀립니다.";

    public InvalidPasswordException() {
        super(MESSAGE);
    }
}
