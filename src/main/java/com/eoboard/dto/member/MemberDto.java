package com.eoboard.dto.member;

import com.eoboard.domain.Role;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MemberDto {

    private Long memberId;
    private String id;
    private String nickName;
    private String email;
    private Role role;

    @QueryProjection
    public MemberDto(Long memberId, String id, String nickName, String email, Role role) {
        this.memberId = memberId;
        this.id = id;
        this.nickName = nickName;
        this.email = email;
        this.role = role;
    }
}
