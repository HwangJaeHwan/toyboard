package com.example.board.toyboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CommentNotFoundException extends ToyboardException {

    public static String MESSAGE = "댓글이 존재하지 않습니다.";
    public CommentNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public String getStatusCode() {
        return null;
    }
}
