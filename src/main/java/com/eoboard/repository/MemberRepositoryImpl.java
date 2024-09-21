package com.eoboard.repository;

import com.eoboard.dto.member.MemberDto;
import com.eoboard.dto.member.QMemberDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.eoboard.domain.QMember.member;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<MemberDto> findAllMembers(Pageable pageable) {
        List<MemberDto> content = queryFactory
                .select(new QMemberDto(
                        member.id.as("member_id"),
                        member.memberId.as("id"),
                        member.nickName,
                        member.email,
                        member.role
                ))
                .from(member)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(member.count())
                .from(member)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
