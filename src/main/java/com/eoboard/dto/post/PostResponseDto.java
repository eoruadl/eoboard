package com.eoboard.dto.post;

import lombok.Data;

@Data
public class PostResponseDto {
    private Long post_id;

    public PostResponseDto(Long post_id) {
        this.post_id = post_id;
    }
}
