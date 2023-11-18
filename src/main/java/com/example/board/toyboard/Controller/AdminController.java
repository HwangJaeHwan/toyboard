package com.example.board.toyboard.Controller;

import com.example.board.toyboard.DTO.PageListDTO;
import com.example.board.toyboard.DTO.SearchDTO;
import com.example.board.toyboard.Service.PostService;
import com.example.board.toyboard.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PostService postService;


    @PostMapping("/users")
    public void users(PageListDTO pageListDTO, SearchDTO searchDTO) {

    }
}
