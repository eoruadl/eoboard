package com.eoboard.controller;

import com.eoboard.domain.Comment;
import com.eoboard.domain.Member;
import com.eoboard.domain.Post;
import com.eoboard.domain.Role;
import com.eoboard.dto.member.MemberRequestDto;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class AdminControllerTest {

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
    public void 멤버_전체_조회() throws Exception {

        mockMvc.perform(get("/admin/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk())
                .andDo(document("admin/members-findAll",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 토큰으로 인증")
                        ),
                        responseFields(
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("응답 내용"),
                                fieldWithPath("content[].memberId").type(JsonFieldType.NUMBER).description("멤버 ID"),
                                fieldWithPath("content[].id").type(JsonFieldType.STRING).description("아이디"),
                                fieldWithPath("content[].nickName").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("content[].email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("content[].role").type(JsonFieldType.STRING).description("권한"),

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
    public void 멤버_수정() throws Exception {
        MemberRequestDto request = new MemberRequestDto();
        request.setMemberId("memberId");
        request.setPassword("1234");
        request.setName("name");
        request.setNickName("nickName");
        request.setEmail("email@gmail.com");
        request.setRole(Role.ADMIN);

        List<Member> members = memberRepository.findAll();
        Long memberId = members.get(0).getId();

        mockMvc.perform(put("/admin/members/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk())
                .andDo(document("admin/member-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 토큰으로 인증")
                        ),
                        requestFields(
                                fieldWithPath("memberId").type(JsonFieldType.STRING).description("멤버 아이디"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("권한")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("멤버 ID")
                        )));
    }

    @Test
    public void 멤버_삭제() throws Exception {

        List<Member> members = memberRepository.findAll();
        Long memberId = members.get(0).getId();

        mockMvc.perform(delete("/admin/members/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk())
                .andDo(document("admin/member-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 토큰으로 인증")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("멤버 ID")
                        )));
    }

    @Test
    @WithMockCustomUser
    public void 게시물_삭제() throws Exception {
        List<Post> allPost = postRepository.findAll();
        Long postId = allPost.get(0).getId();


        mockMvc.perform(delete("/admin/post/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk())
                .andDo(document("admin/post-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 토큰으로 인증")
                        ),
                        responseFields(
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시물_ID")
                        )));
    }

    @Test
    @WithMockCustomUser
    public void 댓글_삭제() throws Exception {
        List<Post> allPost = postRepository.findAll();
        Long postId = allPost.get(0).getId();

        List<Comment> allComment = commentRepository.findAll();
        Long commentId = allComment.get(0).getId();

        mockMvc.perform(delete("/admin/post/" + postId + "/comment/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "JWT"))
                .andExpect(status().isOk())
                .andDo(document("admin/comment-delete",
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
        Member member = Member.builder()
                .memberId("eoruadl")
                .password("1234")
                .nickName("nick")
                .name("name")
                .email("test@gmail.com")
                .build();
        member.updateCreatedAt();
        member.updateRole(Role.ADMIN);
        em.persist(member);
        return member;
    }

    private void initComments(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        String topic = "test";
        String title = "title";
        String content = "content";
//        Post post = Post.createPost(member, topic, title, content);
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
}
