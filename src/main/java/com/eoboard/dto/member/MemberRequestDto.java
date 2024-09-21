package com.eoboard.dto.member;

import com.eoboard.domain.Role;
import lombok.Data;

@Data
public class MemberRequestDto {

    private String memberId;
    private String password;
    private String name;
    private String nickName;
    private String email;
    private Role role;
}
