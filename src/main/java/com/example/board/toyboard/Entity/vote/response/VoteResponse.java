package com.example.board.toyboard.Entity.vote.response;

import com.example.board.toyboard.Entity.vote.VoteType;
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
