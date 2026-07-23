package com.example.board.toyboard.entity.vote.response;

import com.example.board.toyboard.entity.vote.VoteType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VoteResponse {

    VoteType voteType;
    int count;

    @Builder
    public VoteResponse( VoteType voteType, int count) {
        this.voteType = voteType;
        this.count = count;
    }
}
