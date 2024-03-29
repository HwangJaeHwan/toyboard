package com.example.board.toyboard.Repository;

import com.example.board.toyboard.DTO.CommentReadDTO;
import com.example.board.toyboard.DTO.CommentReportDTO;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.QComment;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.board.toyboard.Entity.Report.QCommentReport.*;

@Slf4j
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom{


    private final JPAQueryFactory queryFactory;



    @Override
    public List<CommentReadDTO> commentInfo(Post post) {

        QComment comment = new QComment("comment");

        return queryFactory
                .select(Projections.constructor(CommentReadDTO.class,
                        comment.id,
                        comment.user.nickname,
                        comment.comment,
                        comment.ups.size(),
                        comment.downs.size(),
                        commentReport.count()
                        ))
                .from(comment)
                .join(comment.user)
                .join(commentReport).on(comment.id.eq(commentReport.comment.id))
                .groupBy(comment.id)
                .where(comment.post.eq(post))
                .orderBy(comment.createdTime.asc())
                .fetch();



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
