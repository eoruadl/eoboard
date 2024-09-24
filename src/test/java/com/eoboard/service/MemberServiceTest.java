package com.eoboard.service;

import com.eoboard.domain.Member;
import com.eoboard.domain.Role;
import com.eoboard.dto.member.MemberRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.config.BeanIds;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class MemberServiceTest {
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    EntityManager em;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper om;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) throws ServletException {
        DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy();
        delegatingFilterProxy.init(new MockFilterConfig(webApplicationContext.getServletContext(), BeanIds.SPRING_SECURITY_FILTER_CHAIN));

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .addFilter(delegatingFilterProxy)
                .build();
    }

    @Test
    public void 회원가입() throws Exception {
        MemberRequestDto requestDto = new MemberRequestDto();
        requestDto.setMemberId("eoruadl");
        requestDto.setPassword("1234");
        requestDto.setNickName("nick");
        requestDto.setName("name");
        requestDto.setEmail("test@gmail.com");
        requestDto.setRole(Role.USER);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(document("member/sign-up",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("memberId").type(JsonFieldType.STRING).description("아이디"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일주소"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("권한")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("고유 식별 아이디")
                        )));
    }

    @Test
    public void 회원가입_중복_예외() throws Exception {
        createMember();

        // ID 중복
        Map<String, String> body = new HashMap<>();
        body.put("memberId", "eoruadl");
        body.put("password", "1234");
        body.put("nickName", "nick1");
        body.put("name", "name");
        body.put("email", "test@gmail.com");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().is4xxClientError());

        // 닉네임 중복
        body.put("memberId", "eorua");
        body.put("nickName", "nick");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void 로그인_성공() throws Exception {
        createMember();

        Map<String, String> body = new HashMap<>();
        body.put("memberId", "eoruadl");
        body.put("password", "1234");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andDo(document("member/login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("memberId").type(JsonFieldType.STRING).description("아이디"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드")
                        ),
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("로그인 메시지"),
                                fieldWithPath("memberId").type(JsonFieldType.STRING).description("멤버 아이디")
                        )));
    }

    @Test
    public void 로그인_실패() throws Exception {
        createMember();

        Map<String, String> body = new HashMap<>();
        body.put("memberId", "eoruadl");
        body.put("password", "1235");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().is4xxClientError());
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
//
//    @Test
//    public void 멤버_조회() throws Exception {
//        PageRequest pageRequest = PageRequest.of(0, 3);
//
//        Page<MemberDto> results = memberRepository.findAllMembers(pageRequest);
//
//        for (MemberDto result : results) {
//            System.out.println("result = " + result);
//        }
//
//        List<MemberDto> memberDtoList = results.getContent();
//
//        assertEquals("eoruadl", memberDtoList.get(0).getId());
//        assertEquals("nick", memberDtoList.get(0).getNickName());
//        assertEquals("test@gmail.com", memberDtoList.get(0).getEmail());
//        assertEquals("USER", memberDtoList.get(0).getRole().value());
//
//    }
}
