package com.example.board.toyboard.DTO;

import com.example.board.toyboard.entity.log.Log;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageDTO {
    private Long userId;
    private String nickname;
    private Long postNum;
    private PageConvertDTO<LogDTO, Log> logs;
}
