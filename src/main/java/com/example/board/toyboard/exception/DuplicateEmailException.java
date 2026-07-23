package com.example.board.toyboard.exception;

public class DuplicateEmailException extends ToyboardException {

    public static String MESSAGE = "이미 사용 중인 이메일입니다.";

    public DuplicateEmailException() {
        super(MESSAGE);
    }

    public DuplicateEmailException(String message) {
        super(message);
    }

    @Override
    public String getStatusCode() {
        return null;
    }
}
