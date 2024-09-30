package com.eoboard.controller;

import com.eoboard.domain.Comment;
import com.eoboard.domain.Member;
import com.eoboard.domain.Post;
import com.eoboard.repository.CommentRepository;
import com.eoboard.repository.MemberRepository;
import com.eoboard.repository.PostRepository;
import com.eoboard.service.WithMockCustomUser;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class LikeControllerTest {
    @Autowired
    EntityManager em;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity())
                .build();

        Long memberId = createMember().getId();
        initComments(memberId);
    }

    @Test
    @WithMockCustomUser
    public void 게시물_좋아요() throws Exception {
        List<Post> all = postRepository.findAll();
        Long postId = all.get(0).getId();

        mockMvc.perform(post("/api/v1/post/" + postId + "/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk())
                .andDo(document("like/post-like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 토큰으로 인증"),
                                headerWithName("Content-Type").description("application/json")
                        ),
                        responseFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("결과 내용")
                        )));

    }

    @Test
    @WithMockCustomUser
    public void 게시물_좋아요_취소() throws Exception {
        List<Post> all = postRepository.findAll();
        Long postId = all.get(0).getId();

        mockMvc.perform(post("/api/v1/post/" + postId + "/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/post/" + postId + "/unlike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk())
                .andDo(document("like/post-unlike",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 토큰으로 인증"),
                                headerWithName("Content-Type").description("application/json")
                        ),
                        responseFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("결과 내용")
                        )));
    }

    @Test
    @WithMockCustomUser
    public void 댓글_좋아요() throws Exception {
        List<Post> all = postRepository.findAll();
        Long postId = all.get(0).getId();

        List<Comment> allComment = commentRepository.findAll();
        Long parentId = allComment.get(0).getId();

        mockMvc.perform(post("/api/v1/post/" + postId + "/comment/" + parentId + "/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk())
                .andDo(document("like/comment-like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 토큰으로 인증"),
                                headerWithName("Content-Type").description("application/json")
                        ),
                        responseFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("결과 내용")
                        )));
    }

    @Test
    @WithMockCustomUser
    public void 댓글_좋아요_취소() throws Exception {
        List<Post> all = postRepository.findAll();
        Long postId = all.get(0).getId();

        List<Comment> allComment = commentRepository.findAll();
        Long parentId = allComment.get(0).getId();

        mockMvc.perform(post("/api/v1/post/" + postId + "/comment/" + parentId + "/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/post/" + postId + "/comment/" + parentId + "/unlike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk())
                .andDo(document("like/comment-unlike",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 토큰으로 인증"),
                                headerWithName("Content-Type").description("application/json")
                        ),
                        responseFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("결과 내용")
                        )));
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

    private void initComments(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        Post post = Post.builder()
                .member(member)
                .topic("test")
                .title("title")
                .content("content")
                .build();
        post.updateCreatedAt();
        postRepository.save(post);

        for (int i = 0; i < 3; i++) {
            String commentContent = "comment" + (i + 1);
            Comment comment = new Comment(commentContent, member, post);
            comment.updateCreatedAt();
            commentRepository.save(comment);
        }

        List<Comment> all = commentRepository.findAll();
        Long parentId = all.get(0).getId();

        Comment parentComment = commentRepository.findById(parentId).orElseThrow(NoSuchElementException::new);

        String content = "childContent";
        Comment childComment = new Comment(content, member, post);
        childComment.updateParent(parentComment);
        childComment.updateCreatedAt();
        commentRepository.save(childComment);

        content = "cchildContent";
        Comment cchildComment = new Comment(content, member, post);
        cchildComment.updateParent(childComment);
        cchildComment.updateCreatedAt();
        commentRepository.save(cchildComment);
    }
}
