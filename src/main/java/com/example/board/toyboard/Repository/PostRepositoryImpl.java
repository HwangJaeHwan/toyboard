package com.example.board.toyboard.Repository;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.QPost;
import com.example.board.toyboard.Entity.Post.QRecommendation;
import com.example.board.toyboard.Entity.QComment;
import com.example.board.toyboard.Entity.QUser;
import com.example.board.toyboard.Entity.Report.PostReport;
import com.example.board.toyboard.Entity.Report.QPostReport;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import java.util.List;

import static com.example.board.toyboard.Entity.Post.QPost.post;
import static com.example.board.toyboard.Entity.Post.QRecommendation.*;
import static com.example.board.toyboard.Entity.QUser.*;
import static com.example.board.toyboard.Entity.Report.QPostReport.*;
import static com.querydsl.core.types.ExpressionUtils.count;

@Slf4j
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{


    private final JPAQueryFactory queryFactory;



    @Override
    public Page<PostListDTO> search(SearchDTO searchDTO, Pageable pageable, String postType) {

        QComment comment = new QComment("comment");

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(searchDTO.getType())&&StringUtils.hasText(searchDTO.getContent())) {
            if (searchDTO.getType().equals("t")) {
                builder.and(post.title.contains(searchDTO.getContent()));
            } else {
                builder.and(post.user.nickname.contains(searchDTO.getContent()));
            }
        }

        builder.and(post.postType.eq(postType));

        log.info("ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ");

        List<PostListDTO> content = queryFactory
                .select(Projections.constructor(PostListDTO.class,
                        post.id,
                        post.title,
                        post.user.nickname,
                        post.createdTime,
                        post.hits,
                        comment.count().as("commentNum"),
                        recommendation.count().as("recommendedNumber")
                ))
                .from(post)
                .join(post.user)
                .leftJoin(recommendation).on(post.id.eq(recommendation.post.id))
                .leftJoin(comment).on(post.id.eq(comment.post.id))
                .groupBy(post.id, post.title, post.user.nickname, post.createdTime, post.hits)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(postSort(pageable))
                .fetch();

        log.info("내용 = {}", content);


        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }

    @Override
    public Page<PostReportDTO> reportedList(Pageable pageable) {


        List<PostReportDTO> content = queryFactory
                .select(
                        Projections.constructor(PostReportDTO.class,
                                post.id.as("postId"),
                                post.title.as("title"),
                                post.user.id.as("userId"),
                                post.user.nickname.as("nickname"),
                                postReport.count().as("reposts")))
                .from(post)
                .leftJoin(postReport).on(post.id.eq(postReport.post.id))
                .groupBy(post.id)
                .having(postReport.count().goe(10L))
                .orderBy(postReport.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        JPAQuery<Long> countQuery = queryFactory
                .select(postReport.post.id.count())
                .from(postReport)
                .groupBy(postReport.post.id)
                .having(postReport.post.id.count().goe(10));


        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);




    }

    @Override
    public PostReadDTO postRead(Long postId) {

        PostReadDTO postReadDTO = queryFactory
                .select(Projections.constructor(
                        PostReadDTO.class,
                        post.id.as("id"),
                        post.user.nickname.as("nickname"),
                        post.title.as("title"),
                        post.content.as("content"),
                        post.createdTime.as("createTime"),
                        post.hits.as("hits"),
                        ExpressionUtils.as(
                                JPAExpressions.select(count(recommendation.id))
                                        .from(recommendation)
                                        .where(recommendation.post.id.eq(postId)),
                                "recommendedNumber")
                ))
                .from(post)
                .leftJoin(post.user)
                .where(post.id.eq(postId))
                .fetchOne();


        if (postReadDTO != null) {
            queryFactory.update(post)
                    .set(post.hits, post.hits.add(1))
                    .where(post.id.eq(postId))
                    .execute();
        }

        return postReadDTO;

    }

    private OrderSpecifier<?> postSort(Pageable page) {

        if (!page.getSort().isEmpty()) {

            for (Sort.Order order : page.getSort()) {


                switch (order.getProperty()){
                    case "createdTime":
                        return new OrderSpecifier<>(Order.DESC, post.createdTime);
                    case "hits":
                        return new OrderSpecifier<>(Order.DESC, post.hits);
                    case "recommendedNumber":
                        return new OrderSpecifier<>(Order.DESC, recommendation.count());
                }
            }
        }
        return new OrderSpecifier<>(Order.DESC, post.createdTime);
    }

}
