package com.eoboard.dto.like;

import lombok.Data;

@Data
public class LikeResponseDto {

    private Long likeId;

    public LikeResponseDto(Long likeId) {
        this.likeId = likeId;
    }
}
