package com.eoboard.dto.comment;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostCommentDto {
    private Long commentId;
    private String nickName;
    private String content;
    private Long parentId;
    private List<PostCommentDto> children = new ArrayList<>();
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @QueryProjection
    public PostCommentDto(Long commentId, String nickName, String content, Long parentId, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.commentId = commentId;
        this.nickName = nickName;
        this.content = content;
        this.parentId = parentId;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
