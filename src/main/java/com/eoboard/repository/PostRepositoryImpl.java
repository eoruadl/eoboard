package com.eoboard.repository;

import com.eoboard.dto.comment.PostCommentDto;
import com.eoboard.dto.comment.QPostCommentDto;
import com.eoboard.dto.post.PostDto;
import com.eoboard.dto.post.PostPageDto;
import com.eoboard.dto.post.QPostDto;
import com.eoboard.dto.post.QPostPageDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.eoboard.domain.QComment.comment;
import static com.eoboard.domain.QMember.member;
import static com.eoboard.domain.QPost.post;

public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PostPageDto> findPagePost(Pageable pageable) {
        List<PostPageDto> content = queryFactory
                .select(new QPostPageDto(
                        post.id.as("post_id"),
                        post.topic,
                        post.title,
                        member.id.as("member_id"),
                        member.nickName,
                        post.createdAt,
                        post.updatedAt))
                .from(post)
                .leftJoin(post.member, member)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public PostDto findPost(Long postId) {
        return queryFactory
                .select(new QPostDto(
                        post.id.as("post_id"),
                        post.topic,
                        post.title,
                        post.content,
                        member.id.as("member_id"),
                        member.nickName,
                        post.createdAt,
                        post.updatedAt))
                .from(post)
                .leftJoin(post.member, member)
                .where(post.id.eq(postId))
                .fetchOne();
    }

    @Override
    public List<PostCommentDto> findPostComments(Long postId) {
        return queryFactory
                .select(new QPostCommentDto(comment.id.as("comment_id"),
                        member.nickName,
                        comment.content,
                        comment.parent.id.as("parent_id"),
                        comment.isDeleted,
                        comment.createdAt,
                        comment.updatedAt))
                .from(comment)
                .leftJoin(comment.member, member)
                .where(comment.post.id.eq(postId))
                .orderBy(
                        comment.parent.id.asc().nullsFirst(),
                        comment.createdAt.asc()
                )
                .fetch();
    }
}
