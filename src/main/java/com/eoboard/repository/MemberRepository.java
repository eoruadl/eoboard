package com.eoboard.repository;

import com.eoboard.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select u from User u", Member.class)
                .getResultList();
    }

    public List<Member> findByMemberId(String memberId) {
        return em.createQuery("select m from Member m where m.memberId = :memberId", Member.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<Member> findByNickName(String nickName) {
        return em.createQuery("select m from Member m where m.nickName = :nickName", Member.class)
                .setParameter("nickName", nickName)
                .getResultList();
    }
}
