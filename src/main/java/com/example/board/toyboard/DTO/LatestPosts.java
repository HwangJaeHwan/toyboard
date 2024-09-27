package com.example.board.toyboard.DTO;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LatestPosts {

    List<QnaTitle> qnaList = new ArrayList<>();

    List<FreeTitle> freeList = new ArrayList<>();

    List<NoticeTitle> noticeList = new ArrayList<>();

    List<InfoTitle> infoList = new ArrayList<>();

}
