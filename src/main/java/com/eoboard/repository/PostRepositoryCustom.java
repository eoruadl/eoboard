package com.eoboard.repository;

import com.eoboard.dto.comment.PostCommentDto;
import com.eoboard.dto.post.PostDto;
import com.eoboard.dto.post.PostPageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {
    Page<PostPageDto> findPagePost(Pageable pageable);
    PostDto findPost(Long postId);

    List<PostCommentDto> findPostComments(Long postId);
}
