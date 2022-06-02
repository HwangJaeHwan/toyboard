package com.example.board.toyboard.Controller;


import com.example.board.toyboard.DTO.CommentReadDTO;
import com.example.board.toyboard.DTO.CommentWriteDTO;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Service.*;
import com.example.board.toyboard.session.SessionConst;
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

        log.info("시발2 = {}", post);

        return post.getComments().stream().map(CommentReadDTO::new).collect(Collectors.toList());
    }

    @PostMapping("/{postId}")
    String write(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickname, @PathVariable("postId") Long postId, @RequestBody CommentWriteDTO dto) {

        log.info("시발 = {}", dto.getContent());

        commentService.writeComment(dto, nickname, postId);

        return "Ok";

    }

    @GetMapping("/upButton/{id}")
    Map upButton(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickname, @PathVariable("id") Long id) {

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
    Map downButton(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickname, @PathVariable("id") Long id) {

        HashMap<String, Integer> downCount = new HashMap<>();
        HashMap<String, Boolean> check = new HashMap<>();

        User loginUser = userService.findByNickname(nickname);
        Comment comment = commentService.findById(id);

        if (!downService.downClick(loginUser, comment)) {
            check.put("dup", true);
            return check;
        }

        downCount.put("downCount", comment.getDown());

        return downCount;

    }

    @DeleteMapping("/{id}")
    String delete(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickname, @PathVariable("id") Long id) {

        User loginUser = userService.findByNickname(nickname);


        commentService.delete(id,loginUser);

        return "삭제 완료";
    }

    @PatchMapping("/report/{id}")
    String report(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickname, @PathVariable("id") Long id) {


        User loginUser = userService.findByNickname(nickname);
        Comment comment = commentService.findById(id);

        if (reportService.commentReportCheck(loginUser, comment)) {

            reportService.commentReport(loginUser, comment);

            return "신고 완료";
        }


        return "이미 신고한 댓글입니다.";

    }

}
