package com.example.board.toyboard.DTO;

import org.springframework.data.domain.Pageable;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PageHelper {


    public static PageInfo makePageList(Pageable pageable, int totalPage) {

        PageInfo pageInfo = new PageInfo();

        pageInfo.setNow(pageable.getPageNumber() + 1);


        int tmpEnd = (int) (Math.ceil(pageable.getPageNumber() + 1 / 10.0) * 10);

        pageInfo.setStart(tmpEnd - 9);

        pageInfo.setPrev(pageInfo.getStart() > 1);

        pageInfo.setEnd(Math.min(totalPage, tmpEnd));

        pageInfo.setNext(totalPage > tmpEnd);

        pageInfo.setPageList(IntStream.rangeClosed(pageInfo.getStart(), pageInfo.getEnd()).boxed().collect(Collectors.toList()));

        return pageInfo;

    }


}
