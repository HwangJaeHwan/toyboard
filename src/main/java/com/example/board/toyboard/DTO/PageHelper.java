package com.example.board.toyboard.DTO;

import org.springframework.data.domain.Pageable;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PageHelper {


    public static PageInfo makePageList(Pageable pageable, int totalPage) {

        PageInfo pageInfo = new PageInfo();
        int now = pageable.getPageNumber() + 1;

        pageInfo.setNow(now);


        int tmpEnd = (int) (Math.ceil(now / 10.0) * 10);

        pageInfo.setStart(tmpEnd - 9);

        pageInfo.setPrev(pageInfo.getStart() > 1);

        pageInfo.setEnd(Math.min(totalPage, tmpEnd));

        pageInfo.setNext(totalPage > tmpEnd);

        pageInfo.setPageList(IntStream.rangeClosed(pageInfo.getStart(), pageInfo.getEnd()).boxed().collect(Collectors.toList()));

        return pageInfo;

    }


}
