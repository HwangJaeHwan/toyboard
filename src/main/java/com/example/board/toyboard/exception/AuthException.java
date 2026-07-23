package com.example.board.toyboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class AuthException extends ToyboardException {

    public static String MESSAGE = "권한이 없습니다.";
    public AuthException() {
        super(MESSAGE);
    }

    @Override
    public String getStatusCode() {
        return null;
    }
}
