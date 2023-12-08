package com.example.board.toyboard.Repository;

import com.example.board.toyboard.DTO.SearchDTO;
import com.example.board.toyboard.Entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.board.toyboard.Entity.QUser.user;

@Slf4j
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<User> search(Pageable pageable, SearchDTO searchDTO) {

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(searchDTO.getType())&&StringUtils.hasText(searchDTO.getContent())) {
            if (searchDTO.getType().equals("n")) {
                builder.and(user.nickname.contains(searchDTO.getContent()));
            } else {
                builder.and(user.loginId.contains(searchDTO.getContent()));
            }
        }

        List<User> content = queryFactory
                .selectFrom(user)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }
}
