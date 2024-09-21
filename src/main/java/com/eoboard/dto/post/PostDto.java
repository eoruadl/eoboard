package com.eoboard.dto.post;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {

    private Long postId;
    private String topic;
    private String title;
    private String content;
    private Long memberId;
    private String nickName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @QueryProjection
    public PostDto(Long postId, String topic, String title, String content, Long memberId, String nickName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postId = postId;
        this.topic = topic;
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.nickName = nickName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
