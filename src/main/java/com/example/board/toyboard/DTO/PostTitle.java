package com.example.board.toyboard.DTO;

import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.PostType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostTitle {

    private Long id;
    private String title;
    private PostType postType;


    public PostTitle(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.postType = post.getPostType();

    }
}
