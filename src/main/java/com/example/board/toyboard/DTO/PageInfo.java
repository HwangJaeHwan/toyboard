package com.example.board.toyboard.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
@Data
public class PageInfo {

    private int now;

    private int start, end;

    private boolean prev, next;

    private List<Integer> pageList;


}
