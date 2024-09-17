package com.eoboard.repository;

import com.eoboard.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByMemberId(String memberId);

    List<Member> findByNickName(String nickName);
}
