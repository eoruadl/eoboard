package com.eoboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MemberServiceTest {

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
}
