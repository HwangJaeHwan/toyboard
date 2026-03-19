package com.example.board.toyboard.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.*;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class ApiResponse<T> {

    private String code;
    private String message;
    private T data;



    @Builder
    public ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(String code, String message,T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {

        return of("200", message, data);
    }

    public static <T> ApiResponse<T> create(String message, T data) {

        return of("201", message, data);
    }



}
