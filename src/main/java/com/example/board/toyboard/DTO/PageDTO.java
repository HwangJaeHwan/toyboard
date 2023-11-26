package com.example.board.toyboard.DTO;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.List;


@Slf4j
@Data
public class PageDTO<DTO> {

    private List<DTO> dtoList;

    private int totalPage;

    private PageInfo pageInfo;


    public PageDTO(Page<DTO> dto) {

        totalPage = dto.getTotalPages();

        log.info("totalPage={}", totalPage);

        dtoList = dto.getContent();


        pageInfo = PageHelper.makePageList(dto.getPageable(), totalPage);
    }

}
