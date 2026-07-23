package com.example.board.toyboard.exception;

public class SamePasswordException extends ToyboardException {

    public static String MESSAGE = "현재 비밀번호와 새 비밀번호가 동일합니다.";

    public SamePasswordException() {
        super(MESSAGE);
    }

    @Override
    public String getStatusCode() {
        return null;
    }
}
