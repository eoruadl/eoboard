package com.eoboard.api;

import com.eoboard.domain.Member;
import com.eoboard.domain.Role;
import com.eoboard.dto.member.MemberRequestDto;
import com.eoboard.dto.member.MemberResponseDto;
import com.eoboard.service.MemberService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/api/v1/auth/signup")
    public MemberResponseDto saveMember(@RequestBody @Valid MemberRequestDto request) {

        Member member = new Member(
                request.getMemberId(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickName(),
                request.getName(),
                request.getEmail());

        member.updateCreatedAt();

        if (request.getRole().equals(Role.ADMIN)) {
            member.updateRole(Role.ADMIN);
        }

        Long saveId = memberService.join(member);
        return new MemberResponseDto(saveId);
    }
}

