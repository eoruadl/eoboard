package com.eoboard.dto.comment;

import lombok.Data;

@Data
public class CommentResponseDto {
    private Long commentId;

    public CommentResponseDto(Long commentId) {
        this.commentId = commentId;
    }
}
