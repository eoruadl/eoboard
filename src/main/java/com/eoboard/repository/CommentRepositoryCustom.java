package com.eoboard.repository;

import com.eoboard.domain.Comment;
import com.eoboard.dto.comment.PostCommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryCustom {
    List<PostCommentDto> findCommentsByPostId(Long postId);

    Optional<Comment> findCommentByIdWithParent(Long commentId);
}
