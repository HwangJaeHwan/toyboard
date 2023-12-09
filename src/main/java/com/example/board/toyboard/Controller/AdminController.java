package com.example.board.toyboard.Controller;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Service.CommentService;
import com.example.board.toyboard.Service.PostService;
import com.example.board.toyboard.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;


    @GetMapping("/users")
    public String users(PageListDTO pageListDTO, SearchDTO searchDTO, Model model) {
        Pageable pageable = pageListDTO.getPageable();
        PageConvertDTO<UserListDTO, User> users = userService.makePageResult(pageable, searchDTO);


        log.info("dto = {}", users);
        model.addAttribute("data", users);

        return "auth/home";
    }

    @GetMapping("/posts")
    public String posts(PageListDTO pageListDTO, Model model) {

        PageDTO<PostReportDTO> posts = postService.postsWithReport(pageListDTO);

        model.addAttribute("data", posts);

        return "auth/posts";
    }

    @GetMapping("/comments")
    public String comments(PageListDTO pageListDTO, Model model) {

        PageDTO<CommentReportDTO> comments = commentService.commentsWithReport(pageListDTO);
        model.addAttribute("data", comments);


        return "auth/comments";
    }


}
