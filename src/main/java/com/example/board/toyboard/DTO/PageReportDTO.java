package com.example.board.toyboard.DTO;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Slf4j
@Data
public class PageReportDTO<DTO> {

    List<DTO> dtoList;

    private int totalPage;

    private int now;

    private int start, end;

    private boolean prev, next;

    private List<Integer> pageList;


    public PageReportDTO(Page<DTO> dto) {

        totalPage = dto.getTotalPages();

        log.info("totalPage={}", totalPage);

        dtoList = dto.getContent();


        makePageList(dto.getPageable());
    }



    private void makePageList(Pageable pageable) {


        this.now = pageable.getPageNumber() + 1;

        log.info("now={}", this.now);


        int tmpEnd = (int) (Math.ceil(now / 10.0) * 10);

        log.info("tmpEnd={}", tmpEnd);

        start = tmpEnd - 9;

        prev = start > 1;

        end = Math.min(totalPage, tmpEnd);

        next = totalPage > tmpEnd;

        pageList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

        log.info("start = {}", start);
        log.info("end = {}", end);


    }
}
