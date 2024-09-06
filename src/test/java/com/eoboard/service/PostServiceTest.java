package com.eoboard.service;

import com.eoboard.domain.Member;
import com.eoboard.domain.Post;
import com.eoboard.repository.PostRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PostServiceTest {

    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 게시물_작성() throws Exception {
        //given
        Member member = createMember();
        String topic = "test";
        String title = "게시물";
        String content = "게시물 작성을 테스트합니다.";

        //when
        Long postId = postService.post(member.getId(), topic, title, content);

        //then
        Post getPost = postRepository.findOne(postId);

        assertEquals("test", getPost.getTopic());
        assertEquals("게시물", getPost.getTitle());
        assertEquals("게시물 작성을 테스트합니다.", getPost.getContent());
    }

    @Test
    public void 게시물_전체_조회() throws Exception {
        //given
        Member member = createMember();
        postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");
        postService.post(member.getId(), "test2", "게시물2", "게시물 작성합니다.");
        postService.post(member.getId(), "test3", "게시물3", "게시물 작성합니다.");
        postService.post(member.getId(), "test4", "게시물4", "게시물 작성합니다.");
        postService.post(member.getId(), "test5", "게시물5", "게시물 작성합니다.");

        //when
        List<Post> allPost = postRepository.findAll();

        //then
        assertEquals(5, allPost.size());
    }

    private Member createMember() {
        Member member = new Member();
        member.setMemberId("eoruadl");
        member.setPassword("1234");
        member.setNickName("nick");
        member.setName("name");
        member.setEmail("test@gmail.com");
        em.persist(member);
        return member;
    }
}
