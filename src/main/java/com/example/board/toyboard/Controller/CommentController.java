package com.example.board.toyboard.Controller;


import com.example.board.toyboard.DTO.CommentWriteDTO;
import com.example.board.toyboard.Entity.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Service.CommentService;
import com.example.board.toyboard.Service.PostService;
import com.example.board.toyboard.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    @PostMapping("/write/{postId}")
    String write(@SessionAttribute("loginUser") String nickname, @PathVariable("postId") Long postId, @ModelAttribute(name = "comment") CommentWriteDTO dto) {

        log.info("dto = {}",dto.getComment());

        commentService.writeComment(dto, nickname, postId);


        return "redirect:/post/" + postId;


    }









}