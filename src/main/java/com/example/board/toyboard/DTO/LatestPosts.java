package com.example.board.toyboard.DTO;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LatestPosts {

    List<HomePost> qna = new ArrayList<>();

    List<HomePost> free = new ArrayList<>();

    List<HomePost> notice = new ArrayList<>();

    List<HomePost> info = new ArrayList<>();

}
