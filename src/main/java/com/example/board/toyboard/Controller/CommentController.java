package com.example.board.toyboard.Controller;


import com.example.board.toyboard.DTO.CommentReadDTO;
import com.example.board.toyboard.DTO.CommentWriteDTO;
import com.example.board.toyboard.DTO.DownResponse;
import com.example.board.toyboard.DTO.UpResponse;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.UserType;
import com.example.board.toyboard.Service.*;
import com.example.board.toyboard.session.SessionConst;
import com.example.board.toyboard.session.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final UserService userService;
    private final PostService postService;
    private final ReportService reportService;
    private final CommentService commentService;
    private final UpService upService;
    private final DownService downService;


    @GetMapping("/{postId}")
    List<CommentReadDTO> findAll(@PathVariable("postId") Long postId) {

        Post post = postService.findById(postId);


        return commentService.findComments(postId).stream().map(CommentReadDTO::new).collect(Collectors.toList());
    }

    @PostMapping("/{postId}")
    String write(UserSession userSession, @PathVariable("postId") Long postId, @RequestBody CommentWriteDTO dto) {


        commentService.writeComment(dto, userSession.getUserId(), postId);

        return "Ok";

    }

    @GetMapping("/upButton/{id}")
    UpResponse upButton(UserSession userSession, @PathVariable("id") Long id) {


        return upService.upClick(userSession.getUserId(), id);

    }

    @GetMapping("/downButton/{id}")
    DownResponse downButton(UserSession userSession, @PathVariable("id") Long id) {

        return downService.downClick(userSession.getUserId(), id);

    }

    @DeleteMapping("/{id}")
    String delete(UserSession userSession,
                  @PathVariable("id") Long id) {

        commentService.delete(id, userSession.getUserId(), userSession.getUserType());

        return "삭제 완료";
    }

    @PatchMapping("/report/{id}")
    String report(UserSession userSession, @PathVariable("id") Long id) {


        if (reportService.commentReport(userSession.getUserId(), id)) {

            return "신고 완료";

        }



        return "이미 신고한 댓글입니다.";

    }

}
