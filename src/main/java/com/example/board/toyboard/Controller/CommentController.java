package com.example.board.toyboard.Controller;


import com.example.board.toyboard.DTO.CommentWriteDTO;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final UpService upService;
    private final DownService downService;

    @PostMapping("/write/{postId}")
    String write(@SessionAttribute("loginUser") String nickname, @PathVariable("postId") Long postId, @ModelAttribute(name = "comment") CommentWriteDTO dto) {

        log.info("dto = {}", dto.getComment());

        commentService.writeComment(dto, nickname, postId);


        return "redirect:/post/" + postId;


    }

    @GetMapping("/upButton/{id}")
    @ResponseBody
    Map upButton(@SessionAttribute("loginUser") String nickname, @PathVariable("id") Long id) {

        log.info("tlqkf");
        HashMap<String, Integer> upCount = new HashMap<>();
        HashMap<String, Boolean> check = new HashMap<>();

        User loginUser = userService.findByNickname(nickname);
        Comment comment = commentService.findById(id);

        if (!upService.upClick(loginUser, comment)) {
            check.put("dup", true);
            return check;
        }

        upCount.put("upCount", comment.getUp());

        return upCount;

    }

    @GetMapping("/downButton/{id}")
    @ResponseBody
    Map downButton(@SessionAttribute("loginUser") String nickname, @PathVariable("id") Long id) {

        log.info("tlqkf");
        HashMap<String, Integer> downCount = new HashMap<>();
        HashMap<String, Boolean> check = new HashMap<>();

        User loginUser = userService.findByNickname(nickname);
        Comment comment = commentService.findById(id);

        if (!downService.downClick(loginUser, comment)) {
            check.put("dup", true);
            return check;
        }

        downCount.put("downCount", comment.getUp());

        return downCount;

    }


}
