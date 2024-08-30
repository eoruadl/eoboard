package com.eoboard.api;

import com.eoboard.domain.Member;
import com.eoboard.service.MemberService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/api/v1/auth/signup")
    public CreateMemberResponse saveMember(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setMemberId(request.getMemberId());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setName(request.getName());
        member.setNickName(request.getNickName());
        member.setEmail(request.getEmail());
        member.setCreatedAt(LocalDateTime.now());

        Long saveId = memberService.join(member);
        return new CreateMemberResponse(saveId);
    }

    @PostMapping("api/v1/auth/login")
    public ResponseEntity<String> loginMember(@RequestBody @Valid LoginMemberRequest request) {

        try {
            UsernamePasswordAuthenticationToken authenticationRequest =
                    UsernamePasswordAuthenticationToken.unauthenticated(request.getMemberId(), request.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);


        } catch (Exception e) {
            throw e;
        }
        String msg = "login success";

        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @Data
    static class CreateMemberRequest {
        private String memberId;
        private String password;
        private String name;
        private String nickName;
        private String email;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class LoginMemberRequest {
        private String memberId;
        private String password;
    }

}

