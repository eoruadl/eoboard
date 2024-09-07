package com.eoboard.repository.post.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostCommentQueryDto {

    private Long commentId;
    private String nickName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostCommentQueryDto(Long commentId, String nickName, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.commentId = commentId;
        this.nickName = nickName;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
