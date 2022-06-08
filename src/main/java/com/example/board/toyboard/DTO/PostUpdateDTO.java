package com.example.board.toyboard.DTO;

import com.example.board.toyboard.Entity.Post.Post;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PostUpdateDTO {

    public PostUpdateDTO(Post post) {

        this.postType = post.getPostType();
        this.title = post.getTitle();
        this.content = post.getContent();

    }

    @NotEmpty
    private String postType;

    @NotEmpty(message = "제목을 입력해주세요.")
    private String title;


    @NotEmpty(message = "내용을 입력해주세요.")
    private String content;


}
