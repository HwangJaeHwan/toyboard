package com.example.board.toyboard.DTO;

import com.example.board.toyboard.Entity.Post.Post;
import lombok.Getter;

@Getter
public class HomePost {

    private Long id;

    private String title;

    public HomePost(String item) {
        String[] parts = item.split(":", 2);
        this.id = Long.parseLong(parts[0]);
        this.title = parts[1];
    }

    public HomePost(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();

    }
}
