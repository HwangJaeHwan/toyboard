package com.example.board.toyboard.DTO;

import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.PostType;
import lombok.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class PostUpdateDTO {


    @NotEmpty
    private PostType postType;

    @NotEmpty(message = "제목을 입력해주세요.")
    private String title;


    @NotEmpty(message = "내용을 입력해주세요.")
    private String content;

    public PostUpdateDTO(Post post) {

        this.postType = post.getPostType();
        this.title = post.getTitle();
        this.content = post.getContent();

    }

}
