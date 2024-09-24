package com.eoboard.dto.post;

import lombok.Data;

@Data
public class PostResponseDto {
    private Long postId;

    public PostResponseDto(Long postId) {
        this.postId = postId;
    }
}
