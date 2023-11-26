package com.example.board.toyboard.DTO;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;


@Slf4j
@Data
public class PageConvertDTO<DTO, EN> {

    List<DTO> dtoList;

    private int totalPage;

    private PageInfo pageInfo;


    public PageConvertDTO(Page<EN> entity, Function<EN,DTO> fn) {

        totalPage = entity.getTotalPages();

        log.info("totalPage={}", totalPage);

        dtoList = entity.map(fn).getContent();


        pageInfo = PageHelper.makePageList(entity.getPageable(), totalPage);
    }


}
