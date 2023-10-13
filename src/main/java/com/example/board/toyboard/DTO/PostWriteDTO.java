package com.example.board.toyboard.DTO;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class PostWriteDTO {


    @NotEmpty
    private String postType;


    @NotEmpty(message = "제목을 입력해주세요.")
    private String title;


    @NotEmpty(message = "내용을 입력해주세요.")
    private String content;
}
