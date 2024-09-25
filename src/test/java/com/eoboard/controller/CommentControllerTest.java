package com.eoboard.controller;

import com.eoboard.domain.Comment;
import com.eoboard.domain.Member;
import com.eoboard.domain.Post;
import com.eoboard.dto.comment.CommentRequestDto;
import com.eoboard.repository.CommentRepository;
import com.eoboard.repository.MemberRepository;
import com.eoboard.repository.PostRepository;
import com.eoboard.service.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class CommentControllerTest {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    EntityManager em;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper om;
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
    public void 댓글_생성() throws Exception {
        CommentRequestDto request = new CommentRequestDto();
        request.setContent("댓글 생성");

        List<Post> all = postRepository.findAll();
        Long id = all.get(0).getId();

        mockMvc.perform(post("/api/v1/post/" + id + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk())
                .andDo(document("comment/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 토큰으로 인증"),
                                headerWithName("Content-Type").description("application/json")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 ID")
                        )));
    }

    @Test
    @WithMockCustomUser
    public void 대댓글_생성() throws Exception {
        List<Post> allPost = postRepository.findAll();
        Long postId = allPost.get(0).getId();

        List<Comment> allComment = commentRepository.findAll();
        Long parentId = allComment.get(0).getId();

        CommentRequestDto request = new CommentRequestDto();
        request.setContent("대댓글 생성");

        mockMvc.perform(post("/api/v1/post/" + postId + "/comment/" + parentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk())
                .andDo(document("comment/child-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 토큰으로 인증"),
                                headerWithName("Content-Type").description("application/json")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 ID")
                        )));
    }

    @Test
    @WithMockCustomUser
    public void 댓글_조회() throws Exception {
        List<Post> allPost = postRepository.findAll();
        Long postId = allPost.get(0).getId();

        mockMvc.perform(get("/api/v1/post/" + postId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk())
                .andDo(document("comment/findAll",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 토큰으로 인증")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("댓글 리스트")
                        ).andWithPrefix("[]", commentFields(""))
                                .andWithPrefix("[]children[].", commentFields(""))
                                .andWithPrefix("[]children[].children[].", commentFields(""))));
    }

    @Test
    @WithMockCustomUser
    public void 댓글_수정() throws Exception {
        List<Post> allPost = postRepository.findAll();
        Long postId = allPost.get(0).getId();

        List<Comment> allComment = commentRepository.findAll();
        Long commentId = allComment.get(0).getId();

        CommentRequestDto request = new CommentRequestDto();
        request.setContent("댓글 수정");

        mockMvc.perform(put("/api/v1/post/" + postId + "/comment/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk())
                .andDo(document("comment/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 토큰으로 인증"),
                                headerWithName("Content-Type").description("application/json")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 ID")
                        )));
    }

    @Test
    @WithMockCustomUser
    public void 댓글_삭제() throws Exception {
        List<Post> allPost = postRepository.findAll();
        Long postId = allPost.get(0).getId();

        List<Comment> allComment = commentRepository.findAll();
        Long commentId = allComment.get(0).getId();

        mockMvc.perform(delete("/api/v1/post/" + postId + "/comment/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk())
                .andDo(document("comment/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 토큰으로 인증")
                        ),
                        responseFields(
                                fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 ID")
                        )));
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

    private void initComments(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        String topic = "test";
        String title = "title";
        String content = "content";
        Post post = Post.createPost(member, topic, title, content);
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

        content = "childContent";
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

    private static List<FieldDescriptor> commentFields(String prefix) {
        return Arrays.asList(
                fieldWithPath(prefix + "commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
                fieldWithPath(prefix + "nickName").type(JsonFieldType.STRING).description("생성자 닉네임"),
                fieldWithPath(prefix + "content").type(JsonFieldType.STRING).description("내용"),
                fieldWithPath(prefix + "parentId").type(JsonFieldType.NUMBER).description("부모댓글 ID").optional(),
                fieldWithPath(prefix + "isDeleted").type(JsonFieldType.BOOLEAN).description("삭제여부"),
                fieldWithPath(prefix + "createdAt").type(JsonFieldType.STRING).description("생성일자"),
                fieldWithPath(prefix + "updatedAt").type(JsonFieldType.STRING).description("수정일자").optional(),
                fieldWithPath(prefix + "children").type(JsonFieldType.ARRAY).description("자식댓글")
        );
    }
}
