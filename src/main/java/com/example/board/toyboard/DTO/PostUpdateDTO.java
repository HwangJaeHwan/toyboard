package com.example.board.toyboard.DTO;

import com.example.board.toyboard.entity.post.Post;
import com.example.board.toyboard.entity.post.PostType;
import lombok.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class PostUpdateDTO {


    private Long id;

    @NotNull
    private PostType postType;

    @NotEmpty(message = "제목을 입력해주세요.")
    private String title;


    @NotEmpty(message = "내용을 입력해주세요.")
    private String content;


    public PostUpdateDTO(Post post) {
        this.id = post.getId();
        this.postType = post.getPostType();
        this.title = post.getTitle();
        this.content = post.getContent();

    }

}
