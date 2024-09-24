package com.eoboard.controller;

import com.eoboard.domain.Member;
import com.eoboard.domain.Post;
import com.eoboard.dto.post.PostRequestDto;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.NoSuchElementException;

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
public class PostControllerTest {

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

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity())
                .build();

        Long memberId = createMember().getId();
        initPosts(memberId);
    }

    @Test
    @WithMockCustomUser
    public void 게시물_생성() throws Exception {
        PostRequestDto request = new PostRequestDto();
        request.setTopic("test");
        request.setTitle("post");
        request.setContent("post test");

        mockMvc.perform(post("/api/v1/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("post/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("topic").type(JsonFieldType.STRING).description("토픽"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시물_ID")
                        )));
    }

    @Test
    @WithMockCustomUser
    public void 게시물_전체_조회() throws Exception {

        mockMvc.perform(get("/api/v1/post")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("post/findAll",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("응답 내용"),
                                fieldWithPath("content[].postId").type(JsonFieldType.NUMBER).description("게시물_ID"),
                                fieldWithPath("content[].topic").type(JsonFieldType.STRING).description("토픽"),
                                fieldWithPath("content[].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content[].memberId").type(JsonFieldType.NUMBER).description("멤버_ID"),
                                fieldWithPath("content[].nickName").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("content[].createdAt").type(JsonFieldType.STRING).description("생성일자"),
                                fieldWithPath("content[].updatedAt").type(JsonFieldType.STRING).description("수정일자").optional(),

                                fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("페이징"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("요청 페이지 번호"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지당 항목 개수"),
                                fieldWithPath("pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 조건 유무"),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬X"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬O"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("OFFSET"),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징 적용 여부"),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 적용 여부"),

                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("현재 페이지가 마지막인지 여부"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 데이터 개수"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 개수"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 페이지인지 여부"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지당 항목 개수"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("요청 페이지 번호"),

                                fieldWithPath("sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 조건 유무"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬X"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬O"),

                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지의 항목수"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("현재 페이지가 비어있는지 유무")
                        )));

    }

    @Test
    @WithMockCustomUser
    public void 게시물_단건_조회() throws Exception {
        List<Post> all = postRepository.findAll();
        Long id = all.get(0).getId();

        mockMvc.perform(get("/api/v1/post/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("post/findOne",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시물_ID"),
                                fieldWithPath("topic").type(JsonFieldType.STRING).description("토픽"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("멤버_ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성일자"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정일자").optional())));
    }

    @Test
    @WithMockCustomUser
    public void 게시물_수정() throws Exception {
        List<Post> all = postRepository.findAll();
        Long id = all.get(0).getId();

        PostRequestDto request = new PostRequestDto();
        request.setTopic("test");
        request.setTitle("post");
        request.setContent("post test");

        mockMvc.perform(put("/api/v1/post/" + id).
                        contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("post/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시물_ID")
                                )));
    }

    @Test
    @WithMockCustomUser
    public void 게시물_삭제() throws Exception {
        List<Post> all = postRepository.findAll();
        Long id = all.get(0).getId();

        mockMvc.perform(delete("/api/v1/post/" + id).
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("post/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시물_ID")
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

    private void initPosts(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        String topic = "test";
        for (int i = 0; i < 5; i++) {
            String title = "title" + (i + 1);
            String content = "content" + (i + 1);
            Post post = Post.createPost(member, topic, title, content);
            postRepository.save(post);
        }
    }
}
