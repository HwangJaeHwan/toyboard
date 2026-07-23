package com.example.board.toyboard.exception;

public class DuplicateReportedException extends ToyboardException {

    public static String MESSAGE = "이미 신고한 게시글입니다.";
    public DuplicateReportedException() {
        super(MESSAGE);
    }

    @Override
    public String getStatusCode() {
        return null;
    }
}
