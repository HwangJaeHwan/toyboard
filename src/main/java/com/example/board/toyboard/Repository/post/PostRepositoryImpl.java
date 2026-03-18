package com.example.board.toyboard.Repository.post;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.Post.PostType;
import com.example.board.toyboard.Entity.QComment;
import com.example.board.toyboard.Repository.post.PostRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.board.toyboard.Entity.Post.QPost.post;
import static com.example.board.toyboard.Entity.Post.QRecommendation.*;
import static com.example.board.toyboard.Entity.Report.QPostReport.*;

@Slf4j
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {


    private final JPAQueryFactory queryFactory;



    @Override
    public List<PostTitle> findLatestPostsByType(PostType postType, int limit) {

        return queryFactory.select(Projections.constructor(PostTitle.class,
                        post.id,
                        post.title,
                        post.postType))
                .from(post)
                .where(post.postType.eq(postType))
                .orderBy(post.createdTime.desc())
                .limit(limit)
                .fetch();
    }


    @Override
    public LatestPosts getLatestPosts() {

        LatestPosts latestPosts = new LatestPosts();

        List<PostTitle> allPosts = queryFactory.select(Projections.constructor(PostTitle.class,
                        post.id,
                        post.title,
                        post.postType))
                .from(post)
                .orderBy(post.createdTime.desc())
                .fetch();

        List<PostTitle> free = queryFactory.select(Projections.constructor(PostTitle.class,
                        post.id,
                        post.title,
                        post.postType))
                .from(post)
                .where(post.postType.eq(PostType.FREE))
                .orderBy(post.createdTime.desc())
                .offset(0)
                .limit(5)
                .fetch();

        List<PostTitle> info = queryFactory.select(Projections.constructor(PostTitle.class,
                        post.id,
                        post.title,
                        post.postType))
                .from(post)
                .where(post.postType.eq(PostType.INFO))
                .orderBy(post.createdTime.desc())
                .offset(0)
                .limit(5)
                .fetch();

        List<PostTitle> qna = queryFactory.select(Projections.constructor(PostTitle.class,
                        post.id,
                        post.title,
                        post.postType))
                .from(post)
                .where(post.postType.eq(PostType.QNA))
                .orderBy(post.createdTime.desc())
                .offset(0)
                .limit(5)
                .fetch();

        latestPosts.getFreeList().addAll(free);
        latestPosts.getFreeList().addAll(free);
        latestPosts.getFreeList().addAll(free);


        int freeCount = 0;
        int qnaCount = 0;
        int noticeCount = 0;
        int infoCount = 0;

        for (PostTitle post : allPosts) {
            if (post.getPostType().equals("FREE") && freeCount < 5) {
                latestPosts.getFreeList().add(new FreeTitle(post.getId(), post.getTitle()));
                freeCount++;
            } else if (post.getPostType().equals("QNA") && qnaCount < 5) {
                latestPosts.getQnaList().add(new QnaTitle(post.getId(), post.getTitle()));
                qnaCount++;
            } else if (post.getPostType().equals("NOTICE") && noticeCount < 5) {
                latestPosts.getNoticeList().add(new NoticeTitle(post.getId(), post.getTitle()));
                noticeCount++;
            } else if (post.getPostType().equals("INFO") && infoCount < 5) {
                latestPosts.getInfoList().add(new InfoTitle(post.getId(), post.getTitle()));
                infoCount++;
            }

            // 모든 리스트가 5개씩 채워지면 종료
            if (freeCount == 5 && qnaCount == 5 && noticeCount == 5 && infoCount == 5) {
                break;
            }
        }

        return latestPosts;



    }



    private <T> List<T> getLatestPosts(PostType type, Class<T> clazz) {
        return queryFactory.select(Projections.constructor(clazz,
                        post.id,
                        post.title
                ))
                .from(post)
                .where(post.postType.eq(type))
                .orderBy(post.createdTime.desc())
                .limit(5)
                .fetch();
    }

    @Override
    public Page<PostListDTO> list(Pageable pageable, PostType postType) {

        QComment comment = new QComment("comment");

        BooleanBuilder builder = new BooleanBuilder();

        List<PostListDTO> content = queryFactory
                .select(Projections.constructor(PostListDTO.class,
                        post.id,
                        post.title,
                        post.user.nickname,
                        post.createdTime,
                        post.viewCount,
                        comment.id.count().as("commentNum"),
                        recommendation.id.count().as("recommendedNumber")
                ))
                .from(post)
                .join(post.user)
                .leftJoin(comment).on(comment.post.id.eq(post.id))
                .leftJoin(recommendation).on(recommendation.post.id.eq(post.id))
                .groupBy(post.id, post.title, post.user.nickname, post.createdTime, post.viewCount)
                .where(post.postType.eq(postType))
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
    public Page<PostListDTO> findPosts(Pageable pageable, PostType postType) {

        List<PostListDTO> content = queryFactory
                .select(Projections.constructor(PostListDTO.class,
                        post.id,
                        post.title,
                        post.user.nickname,
                        post.createdTime,
                        post.viewCount
                ))
                .from(post)
                .join(post.user)
                .where(post.postType.eq(postType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(postSort(pageable))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(post.postType.eq(postType));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);


    }

    @Override
    public Map<Long, Long> countCommentsByPostIds(List<Long> ids) {
        QComment comment = QComment.comment1;

        return queryFactory
                .select(post.id, comment.id.count())
                .from(comment)
                .join(comment.post, post)
                .where(post.id.in(ids))
                .groupBy(post.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(post.id),
                        tuple -> tuple.get(comment.id.count())
                ));
    }

    @Override
    public Map<Long, Long> countRecommendationsByPostIds(List<Long> ids) {
        return queryFactory
                .select(post.id, recommendation.id.count())
                .from(recommendation)
                .join(recommendation.post, post)
                .where(post.id.in(ids))
                .groupBy(post.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(post.id),
                        tuple -> tuple.get(recommendation.id.count())
                ));
    }



    @Override
    public Page<PostReportDTO> reportedList(Pageable pageable) {


        List<PostReportDTO> content = queryFactory
                .select(
                        Projections.constructor(PostReportDTO.class,
                                post.id.as("id"),
                                post.title.as("title"),
                                post.user.loginId.as("userLoginId"),
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
                        post.viewCount.as("viewCount"),
                        recommendation.id.countDistinct().as("recommendedNumber")
                ))
                .from(post)
                .leftJoin(post.user)
                .leftJoin(recommendation).on(recommendation.post.id.eq(post.id))
                .where(post.id.eq(postId))
                .groupBy(post.id, post.user.nickname, post.title, post.content, post.createdTime, post.viewCount)
                .fetchOne();


        if (postReadDTO != null) {
            queryFactory.update(post)
                    .set(post.viewCount, post.viewCount.add(1))
                    .where(post.id.eq(postId))
                    .execute();
        }

        return postReadDTO;

    }

    @Override
    public Map<Long, SearchInfo> search(List<Long> ids) {

        QComment comment = new QComment("comment");


        List<Tuple> result = queryFactory.select(
                        post.id,
                        post.viewCount,
                        post.createdTime,
                        recommendation.id.countDistinct()
                )
                .from(post)
                .where(post.id.in(ids))
                .leftJoin(comment).on(comment.post.id.eq(post.id))
                .leftJoin(recommendation).on(recommendation.post.id.eq(post.id))
                .groupBy(post.id, post.title, post.user.nickname, post.createdTime, post.viewCount)
                .fetch();

        return result.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(post.id),
                        tuple -> new SearchInfo(
                                tuple.get(post.id),
                                tuple.get(post.createdTime),
                                tuple.get(post.viewCount),
                                tuple.get(recommendation.id.countDistinct()))
                ));

    }

    @Override
    public PostReadDTO postReadWithHighlight(Long postId, String keyword) {
        return null;
    }


    private OrderSpecifier<?> postSort(Pageable page) {

        if (!page.getSort().isEmpty()) {

            for (Sort.Order order : page.getSort()) {


                switch (order.getProperty()){
                    case "createdTime":
                        return new OrderSpecifier<>(Order.DESC, post.createdTime);
                    case "viewCount":
                        return new OrderSpecifier<>(Order.DESC, post.viewCount);
                    case "recommendedNumber":
                        return new OrderSpecifier<>(Order.DESC, recommendation.count());
                }
            }
        }
        return new OrderSpecifier<>(Order.DESC, post.createdTime);
    }



}
