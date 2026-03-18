package com.example.board.toyboard.DTO;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LatestPosts {

    List<PostTitle> qnaList = new ArrayList<>();

    List<PostTitle> freeList = new ArrayList<>();

    List<PostTitle> noticeList = new ArrayList<>();

    List<PostTitle> infoList = new ArrayList<>();

}
