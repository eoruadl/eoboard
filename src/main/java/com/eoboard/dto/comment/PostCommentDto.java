package com.eoboard.dto.comment;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostCommentDto {
    private Long commentId;
    private String nickName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @QueryProjection
    public PostCommentDto(Long commentId, String nickName, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.commentId = commentId;
        this.nickName = nickName;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
