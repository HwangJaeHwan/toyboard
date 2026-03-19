package com.example.board.toyboard.Entity.Post;

public enum PostType {

    FREE("자유"),INFO("정보"), QNA("질의응답"), NOTICE("공지사항");


    private String korean;

    PostType(String korean) {
        this.korean = korean;
    }

    public String getKorean(){
        return korean;
    }

}
