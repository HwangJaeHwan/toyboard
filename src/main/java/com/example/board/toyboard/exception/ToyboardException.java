package com.example.board.toyboard.exception;

public abstract class ToyboardException extends RuntimeException{

    public ToyboardException(String message) {
        super(message);
    }
    public abstract String getStatusCode();







}
