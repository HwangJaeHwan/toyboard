package com.example.board.toyboard.Exception;

public class SamePasswordException extends RuntimeException {

    public static String MESSAGE = "현재 비밀번호와 새 비밀번호가 동일합니다.";

    public SamePasswordException() {
        super(MESSAGE);
    }
}
