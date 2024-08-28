package com.eoboard.service;

import com.eoboard.domain.Member;
import com.eoboard.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EntityManager em;

    @Test
    public void 회원가입() throws Exception {
        Member member = new Member();
        member.setMemberId("test");
        member.setPassword("test1234");
        member.setEMail("test@gmail.com");
        member.setName("홍길동");
        member.setNickName("신출귀몰");

        Member enMember = Member.createUser(member, passwordEncoder);

        Long saveId = memberService.join(enMember);

        em.flush();

        Member testMember = memberRepository.findOne(saveId);
        assertEquals(enMember, testMember);
    }

    @Test
    public void 회원가입_중복_예외() throws Exception {
        Member member1 = new Member();
        Member member2 = new Member();

        member1.setMemberId("id");
        member1.setPassword("1234");
        member1.setName("test");
        member1.setNickName("nick");

        member2.setMemberId("id");
        member2.setPassword("1234");
        member2.setName("test");
        member2.setNickName("nick");

        //중복 ID 예외
        memberService.join(member1);
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });

        //중복 닉네임 예외
        member2.setMemberId("test2");
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });

    }



}
