package com.eoboard.repository.post.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {

    private final EntityManager em;

    public List<PostQueryDto> findAllPost(int offset, int limit) {
        return em.createQuery(
                        "select new com.eoboard.repository.post.query" +
                                ".PostQueryDto(p.id, p.topic, p.title, p.content, m.id, m.nickName, p.createdAt, p.updatedAt)" +
                                " from Post p" +
                                " join p.member m", PostQueryDto.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public PostQueryDto findPost(Long postId) {
        return em.createQuery(
                        "select new com.eoboard.repository.post.query" +
                                ".PostQueryDto(p.id, p.topic, p.title, p.content, m.id, m.nickName, p.createdAt, p.updatedAt)" +
                                " from Post p" +
                                " join p.member m" +
                                " where p.id = :postId", PostQueryDto.class)
                .setParameter("postId", postId)
                .getSingleResult();
    }

    public List<PostCommentQueryDto> findComments(Long postId) {
        return em.createQuery(
                        "select new com.eoboard.repository.post.query" +
                                ".PostCommentQueryDto(c.id, m.nickName, c.content, c.createdAt, c.updatedAt)" +
                                " from Comment c" +
                                " join c.member m" +
                                " join c.post p" +
                                " where p.id = :postId", PostCommentQueryDto.class)
                .setParameter("postId", postId)
                .getResultList();
    }
}
