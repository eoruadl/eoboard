package com.eoboard.repository;

import com.eoboard.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findByMemberId(String memberId);

    Optional<Member> findByNickName(String nickName);
}
