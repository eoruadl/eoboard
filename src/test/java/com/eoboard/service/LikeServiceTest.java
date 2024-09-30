package com.eoboard.service;

import com.eoboard.domain.*;
import com.eoboard.repository.CommentLikeRepository;
import com.eoboard.repository.CommentRepository;
import com.eoboard.repository.PostLikeRepository;
import com.eoboard.repository.PostRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class LikeServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostLikeRepository postLikeRepository;
    @Autowired
    CommentLikeRepository commentLikeRepository;
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;

    @Test
    public void 게시물_좋아요() throws Exception {
        //given
        Member member = Member.builder()
                .memberId("eoruadl")
                .password("1234")
                .nickName("nick")
                .name("name")
                .email("test@gmail.com")
                .build();
        member.updateCreatedAt();
        em.persist(member);

        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");

        //when
        Long likeId = likeService.likePost(member.getMemberId(), postId);
        PostLike findLike = postLikeRepository.findById(likeId).orElseThrow();
        Post findPost = postRepository.findById(postId).orElseThrow();

        //then
        assertEquals(likeId, findLike.getId());
        assertEquals(findPost.getLikeCount(), 1);
    }

    @Test
    public void 게시물_좋아요_취소() throws Exception {
        //given
        Member member = Member.builder()
                .memberId("eoruadl")
                .password("1234")
                .nickName("nick")
                .name("name")
                .email("test@gmail.com")
                .build();
        member.updateCreatedAt();
        em.persist(member);

        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");
        Long likeId = likeService.likePost(member.getMemberId(), postId);

        //when
        likeService.unlikePost(member.getMemberId(), postId);

        //then
        Optional<PostLike> postLike = postLikeRepository.findById(likeId);
        assertThat(postLike).isEmpty();
        Post findPost = postRepository.findById(postId).orElseThrow();
        assertEquals(findPost.getLikeCount(), 0);
    }

    @Test
    public void 댓글_좋아요() throws Exception {
        //given
        Member member = Member.builder()
                .memberId("eoruadl")
                .password("1234")
                .nickName("nick")
                .name("name")
                .email("test@gmail.com")
                .build();
        member.updateCreatedAt();
        em.persist(member);

        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");
        Long commentId = commentService.createComment(member.getMemberId(), postId, "댓글을 작성합니다.", null);

        //when
        Long likeId = likeService.likeComment(member.getMemberId(), postId, commentId);
        CommentLike commentLike = commentLikeRepository.findById(likeId).orElseThrow();


        //then
        assertEquals(likeId, commentLike.getId());
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        assertEquals(comment.getLikeCount(), 1);
    }

    @Test
    public void 댓글_좋아요_취소() throws Exception {
        //given
        Member member = Member.builder()
                .memberId("eoruadl")
                .password("1234")
                .nickName("nick")
                .name("name")
                .email("test@gmail.com")
                .build();
        member.updateCreatedAt();
        em.persist(member);

        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");
        Long commentId = commentService.createComment(member.getMemberId(), postId, "댓글을 작성합니다.", null);
        Long likeId = likeService.likeComment(member.getMemberId(), postId, commentId);

        //when
        likeService.unlikeComment(member.getMemberId(), postId, commentId);

        //then
        Optional<CommentLike> commentLike = commentLikeRepository.findById(likeId);
        assertThat(commentLike).isEmpty();
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        assertEquals(comment.getLikeCount(), 0);
    }
}
