package com.example.board.toyboard.DTO;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class PageListDTO {

    public int page;

    public PageListDTO() {
        page = 1;
    }


    public Pageable getPageable(Sort sort) {

        return PageRequest.of(page - 1, 10, sort);
    }

}
