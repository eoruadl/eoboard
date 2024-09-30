package com.eoboard.service;

import com.eoboard.domain.Comment;
import com.eoboard.domain.Member;
import com.eoboard.dto.comment.PostCommentDto;
import com.eoboard.repository.CommentRepository;
import com.eoboard.repository.PostRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class CommentServiceTest {

    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 댓글_작성() throws Exception {
        // 댓글 작성
        Member member = createMember();
        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");
        Long commentId = commentService.createComment(member.getMemberId(), postId, "댓글을 작성합니다.", null);

        List<PostCommentDto> comments = postRepository.findPostComments(postId);

        assertEquals(commentId, comments.get(0).getCommentId());
        assertEquals("nick", comments.get(0).getNickName());
        assertEquals("댓글을 작성합니다.", comments.get(0).getContent());

        // 대댓글 작성
        Member member2 = Member.builder()
                .memberId("eorua")
                .password("1234")
                .nickName("nick1")
                .name("name1")
                .email("test1@gmail.com")
                .build();
        member.updateCreatedAt();
        em.persist(member2);

        Long commentId2 = commentService.createComment(member2.getMemberId(), postId, "대댓글을 작성합니다.", commentId);

        Comment comment = commentRepository.findById(commentId2).get();

        assertEquals(commentId2, comment.getId());
        assertEquals("nick1", comment.getMember().getNickName());
        assertEquals("대댓글을 작성합니다.", comment.getContent());
        assertEquals(commentId, comment.getParent().getId());
    }

    @Test
    public void 댓글_조회() throws Exception {
        //given
        Member member = createMember();
        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");
        Long commentId1 = commentService.createComment(member.getMemberId(), postId, "1댓글을 작성합니다.", null);
        Long commentId2 = commentService.createComment(member.getMemberId(), postId, "2댓글을 작성합니다.", null);
        Long commentId3 = commentService.createComment(member.getMemberId(), postId, "3댓글을 작성합니다.", null);
        Long commentId4 = commentService.createComment(member.getMemberId(), postId, "1대댓글을 작성합니다.", commentId1);
        Long commentId5 = commentService.createComment(member.getMemberId(), postId, "2대댓글을 작성합니다.", commentId2);
        Long commentId6 = commentService.createComment(member.getMemberId(), postId, "4대대댓글을 작성합니다.", commentId4);

        //when
        List<PostCommentDto> comments = commentRepository.findCommentsByPostId(postId);

        //then
        assertEquals(commentId1, comments.get(0).getCommentId());
        assertEquals(commentId2, comments.get(1).getCommentId());
        assertEquals(commentId3, comments.get(2).getCommentId());
        assertEquals(commentId4, comments.get(0).getChildren().get(0).getCommentId());
        assertEquals(commentId5, comments.get(1).getChildren().get(0).getCommentId());
        assertEquals(commentId6, comments.get(0).getChildren().get(0).getChildren().get(0).getCommentId());
    }

    @Test
    public void 댓글_수정() throws Exception {
        //given
        Member member = createMember();
        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");
        Long commentId = commentService.createComment(member.getMemberId(), postId, "댓글을 작성합니다.", null);
        Long commentId2 = commentService.createComment(member.getMemberId(), postId, "대댓글을 작성합니다.", commentId);

        //when
        commentService.updateComment(commentId, "댓글을 수정합니다.");
        commentService.updateComment(commentId2, "대댓글을 수정합니다.");
        List<PostCommentDto> comments = commentRepository.findCommentsByPostId(postId);

        //then
        assertEquals(commentId, comments.get(0).getCommentId());
        assertEquals("nick", comments.get(0).getNickName());
        assertEquals("댓글을 수정합니다.", comments.get(0).getContent());
        assertEquals(commentId2, comments.get(0).getChildren().get(0).getCommentId());
        assertEquals("nick", comments.get(0).getChildren().get(0).getNickName());
        assertEquals("대댓글을 수정합니다.", comments.get(0).getChildren().get(0).getContent());
    }

    @Test
    public void 댓글_삭제() throws Exception {
        //given
        Member member = createMember();
        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");
        Long commentId = commentService.createComment(member.getMemberId(), postId, "댓글을 작성합니다.", null);

        //when
        commentService.deleteComment(commentId);
        Optional<Comment> findComment = commentRepository.findById(commentId);

        //then
        Assertions.assertThat(findComment).isEmpty();
    }

    @Test
    public void 댓글_삭제_test() throws Exception {
        //given
        Member member = createMember();
        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");
        Long commentId1 = commentService.createComment(member.getMemberId(), postId, "1댓글을 작성합니다.", null);
        Long commentId2 = commentService.createComment(member.getMemberId(), postId, "2댓글을 작성합니다.", null);
        Long commentId3 = commentService.createComment(member.getMemberId(), postId, "1대댓글을 작성합니다.", commentId1);
        Long commentId4 = commentService.createComment(member.getMemberId(), postId, "1대댓글을 작성합니다.", commentId1);
        Long commentId5 = commentService.createComment(member.getMemberId(), postId, "1대댓글을 작성합니다.", commentId1);
        Long commentId6 = commentService.createComment(member.getMemberId(), postId, "4대댓글을 작성합니다.", commentId4);
        Long commentId7 = commentService.createComment(member.getMemberId(), postId, "5댓글을 작성합니다.", commentId5);

        //2댓글을 삭제
        commentService.deleteComment(commentId2);
        Optional<Comment> findCommentId2 = commentRepository.findById(commentId2);
        Assertions.assertThat(findCommentId2).isEmpty();

        //3댓글을 삭제 (1대댓글) -> 자식 댓글 없음
        commentService.deleteComment(commentId3);
        Optional<Comment> findCommentId3 = commentRepository.findById(commentId3);
        Assertions.assertThat(findCommentId3).isEmpty();
    }

    private Member createMember() {
        Member member = Member.builder()
                .memberId("eoruadl")
                .password("1234")
                .nickName("nick")
                .name("name")
                .email("test@gmail.com")
                .build();
        member.updateCreatedAt();
        em.persist(member);
        return member;
    }
}
