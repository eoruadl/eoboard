package com.eoboard.dto.post;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostPageDto {

    private Long postId;
    private String topic;
    private String title;
    private Long memberId;
    private String nickName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @QueryProjection
    public PostPageDto(Long postId, String topic, String title, Long memberId, String nickName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postId = postId;
        this.topic = topic;
        this.title = title;
        this.memberId = memberId;
        this.nickName = nickName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
