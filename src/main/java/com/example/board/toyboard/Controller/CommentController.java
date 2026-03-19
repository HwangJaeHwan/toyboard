package com.example.board.toyboard.Controller;


import com.example.board.toyboard.DTO.CommentReadDTO;
import com.example.board.toyboard.DTO.CommentWriteDTO;
import com.example.board.toyboard.DTO.DownResponse;
import com.example.board.toyboard.DTO.UpResponse;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.UserType;
import com.example.board.toyboard.Entity.vote.VoteType;
import com.example.board.toyboard.Entity.vote.response.VoteResponse;
import com.example.board.toyboard.Service.*;
import com.example.board.toyboard.response.ApiResponse;
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
@RequestMapping("/comments")
public class CommentController {

    private final ReportService reportService;
    private final CommentService commentService;
    private final VoteService voteService;

    @GetMapping("/{postId}")
    ApiResponse<List<CommentReadDTO>> getComments(@PathVariable("postId") Long postId) {

        return ApiResponse.success("댓글 리스트", commentService.findComments(postId));
    }

    @GetMapping("/{commentId}/replies")
    ApiResponse<List<CommentReadDTO>> getReplies(@PathVariable Long commentId) {

        return ApiResponse.success("대댓글 리스트", commentService.getReplies(commentId));
    }

    @PostMapping("/{postId}")

    ApiResponse<Long> write(UserSession userSession, @PathVariable("postId") Long postId, @RequestBody CommentWriteDTO dto) {

        Long commentId = commentService.writeComment(dto, userSession.getId(), postId);

        return ApiResponse.create("댓글 작성 완료",commentId);

    }

//<<<<<<< Updated upstream
//    @GetMapping("/upButton/{id}")
//    UpResponse upButton(UserSession userSession, @PathVariable("id") Long id) {
//
//
//        return upService.upClick(userSession.getUserId(), id);
//
//    }
//
//    @GetMapping("/downButton/{id}")
//    DownResponse downButton(UserSession userSession, @PathVariable("id") Long id) {
//
//        return downService.downClick(userSession.getUserId(), id);
//=======
    @PostMapping("/{commentId}/replies")
    ApiResponse<Long> writeReply(UserSession userSession, @PathVariable("commentId") Long commentId, @RequestBody CommentWriteDTO dto) {

        Long replyId = commentService.writeReply(dto, userSession.getNickname(), commentId);

        return ApiResponse.create("대댓글 작성 완료", replyId);
    }


    @GetMapping("/{id}/ups")
    ApiResponse<VoteResponse> up(UserSession userSession, @PathVariable("id") Long commentId){

        return voteService.vote(userSession.getNickname(), commentId, VoteType.UP);

    }

    @GetMapping("/{id}/downs")
    ApiResponse<VoteResponse> down(UserSession userSession, @PathVariable("id") Long commentId){

        return voteService.vote(userSession.getNickname(), commentId, VoteType.DOWN);

    }



//    @GetMapping("/upButton/{id}")
//    Map upButton(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickname, @PathVariable("id") Long id) {
//
//        HashMap<String, Integer> upCount = new HashMap<>();
//        HashMap<String, Boolean> check = new HashMap<>();
//
//        User loginUser = userService.findByNickname(nickname);
//        Comment comment = commentService.findWithPostAndUser(id);
//
//        if (!upService.upClick(loginUser, comment)) {
//            check.put("dup", true);
//            return check;
//        }
//
//        upCount.put("upCount", comment.getUps().size());
//
//        return upCount;
//
//    }
//
//    @GetMapping("/downButton/{id}")
//    Map downButton(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickname, @PathVariable("id") Long id) {
//
//        HashMap<String, Integer> downCount = new HashMap<>();
//        HashMap<String, Boolean> check = new HashMap<>();
//
//        User loginUser = userService.findByNickname(nickname);
//        Comment comment = commentService.findById(id);
//
//        if (!downService.downClick(loginUser, comment)) {
//            check.put("dup", true);
//            return check;
//        }
//
//        downCount.put("downCount", comment.getDowns().size());
//
//        return downCount;
//
//    }

    @DeleteMapping("/{id}")
    ApiResponse<Long> delete(@PathVariable("id") Long id, UserSession userSession) {

        Long deleteCommentId = commentService.delete(id, userSession.getId(), userSession.getUserType());


        return ApiResponse.success("삭제 완료", deleteCommentId);
    }


    @PatchMapping("/{id}/reports")
    ApiResponse<Long> report(UserSession userSession, @PathVariable("id") Long id) {

        return reportService.commentReport(userSession.getNickname(), id);
    }

}
