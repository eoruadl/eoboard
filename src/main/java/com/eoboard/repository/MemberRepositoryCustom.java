package com.eoboard.repository;

import com.eoboard.dto.member.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

    Page<MemberDto> findAllMembers(Pageable pageable);
}
