package com.eoboard.dto.post;

import lombok.Data;

@Data
public class PostRequestDto {
    private String topic;
    private String title;
    private String content;
}
