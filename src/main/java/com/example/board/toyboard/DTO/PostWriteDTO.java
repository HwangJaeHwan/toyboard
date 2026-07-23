package com.example.board.toyboard.DTO;

import com.example.board.toyboard.entity.post.PostType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class PostWriteDTO {


    @NotNull(message = "타입을 지정해주세요.")
    private PostType postType;


    @NotEmpty(message = "제목을 입력해주세요.")
    private String title;


    @NotEmpty(message = "내용을 입력해주세요.")
    private String content;

    @Builder
    public PostWriteDTO(PostType postType, String title, String content) {
        this.postType = postType;
        this.title = title;
        this.content = content;
    }
}
