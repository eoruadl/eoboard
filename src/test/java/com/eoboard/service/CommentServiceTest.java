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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

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
        Member member2 = new Member();
        member2.setMemberId("eorua");
        member2.setPassword(bCryptPasswordEncoder.encode("1234"));
        member2.setNickName("nick1");
        member2.setName("name1");
        member2.setEmail("test1@gmail.com");
        em.persist(member2);

        Long commentId2 = commentService.createComment(member2.getMemberId(), postId, "대댓글을 작성합니다.", commentId);

        Comment comment = commentRepository.findById(commentId2).get();

        assertEquals(commentId2, comment.getId());
        assertEquals("nick1", comment.getMember().getNickName());
        assertEquals("대댓글을 작성합니다.", comment.getContent());
        assertEquals(commentId, comment.getParent().getId());

    }

    @Test
    public void 댓글_수정() throws Exception {
        //given
        Member member = createMember();
        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");
        Long commentId = commentService.comment(member.getMemberId(), postId, "댓글을 작성합니다.");

        //when
        commentService.updateComment(commentId, "댓글을 수정합니다.");
        List<PostCommentDto> comments = postRepository.findPostComments(postId);

        //then
        assertEquals(commentId, comments.get(0).getCommentId());
        assertEquals("nick", comments.get(0).getNickName());
        assertEquals("댓글을 수정합니다.", comments.get(0).getContent());
    }

    @Test
    public void 댓글_삭제() throws Exception {
        //given
        Member member = createMember();
        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");
        Long commentId = commentService.comment(member.getMemberId(), postId, "댓글을 작성합니다.");

        //when
        commentService.deleteComment(commentId);
        Optional<Comment> findComment = commentRepository.findById(commentId);

        //then
        Assertions.assertThat(findComment).isEmpty();
    }

    private Member createMember() {
        Member member = new Member();
        member.setMemberId("eoruadl");
        member.setPassword(bCryptPasswordEncoder.encode("1234"));
        member.setNickName("nick");
        member.setName("name");
        member.setEmail("test@gmail.com");
        em.persist(member);
        return member;
    }
}
