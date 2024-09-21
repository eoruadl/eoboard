package com.eoboard.service;

import com.eoboard.domain.Member;
import com.eoboard.dto.member.MemberDto;
import com.eoboard.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MemberServiceTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper om;

    @BeforeEach
    public void 회원가입() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("memberId", "eoruadl");
        body.put("password", "1234");
        body.put("nickName", "nick");
        body.put("name", "name");
        body.put("email", "test@gmail.com");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isOk());

    }

    @Test
    public void 회원가입_중복_예외() throws Exception {

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

        Map<String, String> body = new HashMap<>();
        body.put("memberId", "eoruadl");
        body.put("password", "1234");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    public void 로그인_실패() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("memberId", "eoruadl");
        body.put("password", "1235");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void 멤버_조회() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<MemberDto> results = memberRepository.findAllMembers(pageRequest);

        for (MemberDto result : results) {
            System.out.println("result = " + result);
        }

        List<MemberDto> memberDtoList = results.getContent();

        assertEquals("eoruadl", memberDtoList.get(0).getId());
        assertEquals("nick", memberDtoList.get(0).getNickName());
        assertEquals("test@gmail.com", memberDtoList.get(0).getEmail());
        assertEquals("USER", memberDtoList.get(0).getRole().value());

    }
}
