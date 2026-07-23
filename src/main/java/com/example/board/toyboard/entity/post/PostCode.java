package com.example.board.toyboard.entity.post;

import lombok.Data;

@Data
public class PostCode {

    private String postType;
    private String displayName;

    public PostCode(String postType, String displayName) {
        this.postType = postType;
        this.displayName = displayName;
    }
}
