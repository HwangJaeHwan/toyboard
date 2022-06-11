package com.example.board.toyboard.DTO;

import com.example.board.toyboard.Entity.log.Log;
import com.example.board.toyboard.Entity.log.LogType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogDTO {

    Long postId;

    String postTitle;

    String message;

    String commentWriter;

    Long commentWriterId;

    LocalDateTime createTime;

    boolean commentCheck;


    public LogDTO(Log log) {

        this.postId = log.getPost().getId();
        this.postTitle = log.getPost().getTitle();
        this.createTime = log.getCreatedTime();

        switch (log.getLogType()) {

            case UP:
            case DOWN:
                if (log.getLogType() == LogType.UP) {
                    this.message = "님의 댓글을 UP 했습니다.";
                } else {
                    this.message = "님의 댓글을 DOWN 했습니다.";
                }
                this.commentWriter = log.getComment().getUser().getNickname();
                this.commentWriterId = log.getComment().getUser().getId();
                this.commentCheck = true;

                break;

            case POST:
            case COMMENT:
            case RECOMMEND:

                if (log.getLogType() == LogType.RECOMMEND) {
                    this.message = "게시글을 추천했습니다.";
                } else if (log.getLogType() == LogType.COMMENT) {
                    this.message = "게시글에 댓글을 달았습니다.";
                } else {
                    this.message = "게시글을 작성했습니다.";
                }

                this.commentCheck = false;

                break;
        }


    }


}
