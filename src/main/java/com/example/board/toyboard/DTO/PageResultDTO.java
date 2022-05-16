package com.example.board.toyboard.DTO;

import com.example.board.toyboard.Entity.Post.Post;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@Slf4j
public class PageResultDTO {


    List<PostListDTO> dtoList;

    private int totalPage;

    private int now;

    private int start, end;

    private boolean prev, next;

    private List<Integer> pageList;


    public PageResultDTO(Page<Post> posts) {

        totalPage = posts.getTotalPages();

        log.info("totalPage={}", totalPage);


        dtoList = posts.map(post -> new PostListDTO(post, post.getUser().getNickname())).getContent();

        makePageList(posts.getPageable());
    }


    private void makePageList(Pageable pageable) {


        this.now = pageable.getPageNumber() + 1;

        log.info("now={}", this.now);


        int tmpEnd = (int) (Math.ceil(now / 10.0) * 10);

        log.info("tmpEnd={}", tmpEnd);

        start = tmpEnd - 9;

        prev = start > 1;

        end = totalPage > tmpEnd ? tmpEnd : totalPage;

        next = totalPage > tmpEnd;

        pageList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

        log.info("start = {}", start);
        log.info("end = {}", end);


    }


}
