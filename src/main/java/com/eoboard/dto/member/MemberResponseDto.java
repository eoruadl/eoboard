package com.eoboard.dto.member;

import lombok.Data;

@Data
public class MemberResponseDto {
    private Long id;

    public MemberResponseDto(Long id) {
        this.id = id;
    }
}
