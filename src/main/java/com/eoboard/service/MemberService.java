package com.eoboard.service;

import com.eoboard.domain.Member;
import com.eoboard.domain.Role;
import com.eoboard.dto.member.MemberRequestDto;
import com.eoboard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 가입
     */

    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);
        validateDuplicateNickName(member);
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 회원 정보 수정
     */

    @Transactional
    public void updateMember(Long memberId, MemberRequestDto request) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);

        findMember.updateMemberId(request.getMemberId());
        findMember.updatePassword(passwordEncoder.encode(request.getPassword()));
        findMember.updateNickName(request.getNickName());
        findMember.updateEmail(request.getEmail());
        if (request.getRole().equals(Role.ADMIN)) {
            findMember.updateRole(Role.ADMIN);
        } else {
            findMember.updateRole(Role.USER);
        }
        findMember.updateUpdatedAt();

    }

    /**
     * 회원 삭제
     */
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        memberRepository.delete(member);
    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> findMembers = memberRepository.findByMemberId(member.getMemberId());
        if (!findMembers.isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
    }

    private void validateDuplicateNickName(Member member) {
        Optional<Member> findMembers = memberRepository.findByNickName(member.getNickName());
        if (!findMembers.isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
    }

    public Member findOneByMemberId(String memberId) {
        return memberRepository.findByMemberId(memberId).get();
    }
}
