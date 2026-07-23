package com.example.board.toyboard.exception;

public class DuplicateLoginIdException extends ToyboardException {

    public static String MESSAGE = "이미 사용 중인 아이디입니다.";
    public DuplicateLoginIdException() {
        super(MESSAGE);
    }

    public DuplicateLoginIdException(String message) {
        super(message);
    }

    @Override
    public String getStatusCode() {
        return null;
    }
}
