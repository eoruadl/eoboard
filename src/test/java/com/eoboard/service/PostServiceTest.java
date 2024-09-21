package com.eoboard.service;

import com.eoboard.domain.Member;
import com.eoboard.domain.Post;
import com.eoboard.dto.post.PostDto;
import com.eoboard.dto.post.PostPageDto;
import com.eoboard.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class PostServiceTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    EntityManager em;
    @Autowired
    ObjectMapper om;

    @WithMockCustomUser
    @Test
    public void 게시물_작성_API() throws Exception {
        Member member = createMember();

        Map<String, String> body = new HashMap<>();
        body.put("topic", "test");
        body.put("title", "게시물 작성");
        body.put("content", "게시물 작성 테스트다.");


        mockMvc.perform(post("/api/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

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
        Post getPost = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);

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

    @Test
    public void 게시물_페이지_조회() throws Exception {
        //given
        Member member = createMember();
        postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");
        postService.post(member.getId(), "test2", "게시물2", "게시물 작성합니다.");
        postService.post(member.getId(), "test3", "게시물3", "게시물 작성합니다.");
        postService.post(member.getId(), "test4", "게시물4", "게시물 작성합니다.");
        postService.post(member.getId(), "test5", "게시물5", "게시물 작성합니다.");

        PageRequest pageRequest = PageRequest.of(0, 3);

        //when
        Page<PostPageDto> result = postRepository.findPagePost(pageRequest);

        //then
        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getContent()).extracting("title").containsExactly("게시물1", "게시물2", "게시물3");
    }

    @Test
    public void 게시물_조회() throws Exception {
        //given
        Member member = createMember();
        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");

        //when
        Post findPost = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);

        //then
        assertEquals("test1", findPost.getTopic());
        assertEquals("게시물1", findPost.getTitle());
        assertEquals("게시물 작성합니다.", findPost.getContent());
    }

    @Test
    public void Q_게시물_단건_조회() throws Exception {
        //given
        Member member = createMember();
        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");

        //when
        PostDto findPost = postRepository.findPost(postId);

        System.out.println(findPost);

        //then
        assertEquals("test1", findPost.getTopic());
        assertEquals("게시물1", findPost.getTitle());
        assertEquals("게시물 작성합니다.", findPost.getContent());
        assertEquals("nick", findPost.getNickName());
    }

    @Test
    public void 게시물_수정() throws Exception {
        //given
        Member member = createMember();
        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");

        //when
        postService.updatePost(postId, "test1", "게시물수정", "게시물 수정합니다.");

        //then
        Post findPost = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);
        assertEquals("게시물수정", findPost.getTitle());
        assertEquals("게시물 수정합니다.", findPost.getContent());
    }

    @Test
    public void 게시물_삭제() throws Exception {
        //given
        Member member = createMember();
        Long postId = postService.post(member.getId(), "test1", "게시물1", "게시물 작성합니다.");

        //when
        postService.deletePost(postId);

        //then
        Optional<Post> findPost = postRepository.findById(postId);
        assertThat(findPost).isEmpty();
    }

    private Member createMember() {
        Member member = new Member(
                "eoruadl",
                bCryptPasswordEncoder.encode("1234"),
                "nick",
                "name",
                "test@gmail.com");
        em.persist(member);
        return member;
    }
}
