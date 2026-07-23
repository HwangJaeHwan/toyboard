package com.example.board.toyboard.repository;

import com.example.board.toyboard.DTO.CommentReportDTO;

import com.example.board.toyboard.entity.QComment;
import com.example.board.toyboard.entity.vote.QVote;
import com.example.board.toyboard.entity.vote.VoteType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.board.toyboard.entity.QDown.down;
import static com.example.board.toyboard.entity.QUp.up;
import static com.example.board.toyboard.entity.report.QCommentReport.commentReport;


@Slf4j
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom{


    private final JPAQueryFactory queryFactory;




    @Override
    public Map<Long, Long> getUpCounts(List<Long> commentIds) {
        return getVoteCounts(commentIds, VoteType.UP);
    }

    @Override
    public Map<Long, Long> getDownCounts(List<Long> commentIds) {
        return getVoteCounts(commentIds, VoteType.DOWN);
    }

    private Map<Long, Long> getVoteCounts(List<Long> commentIds, VoteType voteType) {
        QComment comment = QComment.comment1;
        QVote vote = QVote.vote;

        return queryFactory
                .select(comment.id, vote.count())
                .from(vote)
                .join(vote.comment, comment)
                .where(
                        comment.id.in(commentIds),
                        vote.voteType.eq(voteType)
                )
                .groupBy(comment.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(comment.id),
                        tuple -> tuple.get(vote.count())
                ));
    }

    @Override
    public Map<Long, Long> getReportCounts(List<Long> commentIds) {
        QComment comment = new QComment("comment");
        return queryFactory
                .select(comment.id, commentReport.count())
                .from(commentReport)
                .join(commentReport.comment, comment)
                .where(comment.id.in(commentIds))
                .groupBy(comment.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(comment.id),
                        tuple -> tuple.get(commentReport.count())
                ));
    }


    @Override
    public Map<Long, Long> getReplyCounts(List<Long> commentIds) {
        QComment comment = new QComment("comment");
        QComment reply = new QComment("reply");

        return queryFactory
                .select(comment.id, reply.count())
                .from(reply)
                .join(reply.parent, comment)
                .where(comment.id.in(commentIds))
                .groupBy(comment.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(comment.id),
                        tuple -> tuple.get(reply.count())
                ));
    }


    @Override
    public Page<CommentReportDTO> commentReports(Pageable pageable) {

        QComment comment = new QComment("comment");

        List<CommentReportDTO> content = queryFactory
                .select(
                        Projections.constructor(CommentReportDTO.class,
                                comment.id.as("id"),
                                comment.post.id.as("postId"),
                                comment.post.title.as("postTitle"),
                                comment.comment.as("comment"),
                                comment.user.nickname.as("nickname"),
                                commentReport.count().as("reports")
                        ))
                .from(comment)
                .leftJoin(commentReport).on(comment.id.eq(commentReport.comment.id))
                .groupBy(comment.id)
                .having(commentReport.count().goe(10L))
                .orderBy(commentReport.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> ids = queryFactory
                .select(comment.id)
                .from(comment)
                .join(commentReport).on(comment.id.eq(commentReport.comment.id))
                .groupBy(comment.id)
                .having(commentReport.count().goe(10L))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.id.in(ids));

        log.info("카운터 = {}", ids);


        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }



}
