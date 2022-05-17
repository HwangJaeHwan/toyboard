package com.example.board.toyboard.Repository;

import com.example.board.toyboard.DTO.SearchDTO;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.QPost;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.board.toyboard.Entity.Post.QPost.post;

@Slf4j
public class PostRepositoryImpl implements PostRepositoryCustom{


    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Page<Post> search(SearchDTO searchDTO, Pageable pageable, String postType) {

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(searchDTO.getType())&&StringUtils.hasText(searchDTO.getContent())) {
            if (searchDTO.getType().equals("t")) {
                builder.and(post.title.contains(searchDTO.getContent()));
            } else {
                builder.and(post.user.nickname.contains(searchDTO.getContent()));
            }
        }

        builder.and(post.postType.eq(postType));

        List<Post> content = queryFactory
                .selectFrom(post)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();



        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }

}
