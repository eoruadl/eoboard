package com.eoboard.repository;

import com.eoboard.domain.Comment;
import com.eoboard.dto.comment.PostCommentDto;
import com.eoboard.dto.comment.QPostCommentDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.*;

import static com.eoboard.domain.QComment.comment;
import static com.eoboard.domain.QMember.member;

public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CommentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<PostCommentDto> findCommentsByPostId(Long postId) {
        List<PostCommentDto> comments = queryFactory
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

        List<PostCommentDto> postCommentDtoList = new ArrayList<>();
        Map<Long, PostCommentDto> postCommentDtoMap = new HashMap<>();

        comments.forEach(comment -> {
            postCommentDtoMap.put(comment.getCommentId(), comment);
            if (comment.getParentId() != null) postCommentDtoMap.get(comment.getParentId()).getChildren().add(comment);
            else postCommentDtoList.add(comment);
        });
        return postCommentDtoList;
    }

    @Override
    public Optional<Comment> findCommentByIdWithParent(Long commentId) {
        Comment findComment = queryFactory
                .select(comment)
                .from(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.id.eq(commentId))
                .fetchOne();
        return Optional.ofNullable(findComment);
    }
}
