package com.eoboard.dto.like;

import lombok.Data;

@Data
public class LikeResponseDto {

    private String content;

    public LikeResponseDto(String content) {
        this.content = content;
    }
}
