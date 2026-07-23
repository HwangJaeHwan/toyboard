package com.example.board.toyboard.exception;

public class DuplicateNicknameException extends ToyboardException {

    public static String MESSAGE = "이미 사용 중인 닉네임입니다.";
    public DuplicateNicknameException() {
        super(MESSAGE);
    }

    public DuplicateNicknameException(String message) {
        super(message);
    }

    @Override
    public String getStatusCode() {
        return null;
    }
}
