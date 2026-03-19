package com.example.board.toyboard.Service;

import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Down;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.Log;
import com.example.board.toyboard.Entity.log.LogType;
import com.example.board.toyboard.Entity.vote.Vote;
import com.example.board.toyboard.Entity.vote.VoteType;
import com.example.board.toyboard.Entity.vote.response.VoteResponse;
import com.example.board.toyboard.Exception.CommentNotFoundException;
import com.example.board.toyboard.Exception.UserNotFoundException;
import com.example.board.toyboard.Repository.CommentRepository;
import com.example.board.toyboard.Repository.LogRepository;
import com.example.board.toyboard.Repository.UserRepository;
import com.example.board.toyboard.Repository.VoteRepository;
import com.example.board.toyboard.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class VoteService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LogRepository logRepository;
    private final VoteRepository voteRepository;


    @Transactional
    public ApiResponse<VoteResponse> vote(String nickname, Long CommentId, VoteType voteType) {

        User user = userRepository.findByNickname(nickname).orElseThrow(UserNotFoundException::new);
        Comment comment = commentRepository.findById(CommentId).orElseThrow(CommentNotFoundException::new);

        Vote vote = voteRepository
                .findByUserAndComment(user, comment)
                .orElse(null);

        String message;

        if (vote != null) {

            if (vote.getVoteType() != voteType) {
                message = createMessage(voteType, true);
            } else {
                voteRepository.delete(vote);
                deleteLog(user, comment, voteType);
                message = "해당 투표를 취소합니다.";
            }

        } else {

            Vote newVote = new Vote(user, comment, voteType);
            voteRepository.save(newVote);

            createLog(user, comment, voteType);
            message = createMessage(voteType, false);
        }

        int count = voteRepository
                .countByCommentAndVoteType(comment, voteType)
                .intValue();

        return ApiResponse.create(
                message,
                VoteResponse.builder()
                        .voteType(voteType)
                        .count(count)
                        .build());
    }

    private String createMessage(VoteType voteType, boolean flag) {

        if (flag) {

            return voteType == VoteType.DOWN ? "이미 UP한 댓글입니다." : "이미 DOWN한 댓글입니다.";
        }

        return voteType == VoteType.DOWN ? "DOWN 완료" : "UP 완료";

    }


    private void createLog(User user, Comment comment, VoteType voteType) {

        Post post = comment.getPost();

        LogType logType = voteType == VoteType.UP
                ? LogType.UP
                : LogType.DOWN;

        Log log = new Log(user, post, logType, comment);

        comment.addLog(log);
        logRepository.save(log);
    }

    private void deleteLog(User user, Comment comment, VoteType voteType) {

        LogType logType = voteType == VoteType.UP
                ? LogType.UP
                : LogType.DOWN;

        logRepository
                .findLogByUserAndCommentAndLogType(user, comment, logType)
                .ifPresent(log -> {
                    comment.removeLog(log);
                    logRepository.delete(log);
                });
    }




}
