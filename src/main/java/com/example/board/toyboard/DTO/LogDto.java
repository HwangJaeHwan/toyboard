package com.example.board.toyboard.DTO;

import com.example.board.toyboard.Entity.log.Log;
import com.example.board.toyboard.Entity.log.LogType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogDto {

    Long postId;

    String postTitle;

    String message;

    String commentWriter;

    Long commentWriterId;

    LocalDateTime createTime;

    boolean commentCheck;


    public static LogDto createCommentDTO(Log log) {

        LogDto logDto = new LogDto();
        logDto.setCreateTime(log.getCreatedTime());
        logDto.setPostTitle(log.getPost().getTitle());
        logDto.setPostId(log.getPost().getId());

        if (log.getLogType() == LogType.UP) {
            logDto.setMessage("님의 댓글을 UP 했습니다.");
        } else {
            logDto.setMessage("님의 댓글을 DOWN 했습니다.");
        }

        logDto.setCommentWriter(log.getComment().getUser().getNickname());
        logDto.setCommentWriterId(log.getComment().getUser().getId());
        logDto.commentCheck = true;

        return logDto;
    }

    public static LogDto createPostDTO(Log log){

        LogDto logDto = new LogDto();
        logDto.setCreateTime(log.getCreatedTime());
        logDto.setPostTitle(log.getPost().getTitle());
        logDto.setPostId(log.getPost().getId());

        if (log.getLogType() == LogType.RECOMMEND) {
            logDto.setMessage("게시글을 추천했습니다..");
        } else if (log.getLogType() == LogType.COMMENT) {
            logDto.setMessage("게시글에 댓글을 달았습니다.");
        } else {
            logDto.setMessage("게시글을 작성했습니다.");
        }
        logDto.commentCheck = false;

        return logDto;
    }
}
