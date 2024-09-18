package com.eoboard.repository;

import com.eoboard.dto.comment.PostCommentDto;

import java.util.List;

public interface CommentRepositoryCustom {
    List<PostCommentDto> findCommentsByPostId(Long postId);
}
