package com.eoboard.api;

import com.eoboard.dto.like.LikeResponseDto;
import com.eoboard.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LikeApiController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}/like")
    public LikeResponseDto likePost(@PathVariable("postId") Long postId, Principal principal) {
        likeService.likePost(principal.getName(), postId);
        return new LikeResponseDto("게시물 좋아요 성공");
    }

    @DeleteMapping("/post/{postId}/unlike")
    public LikeResponseDto unlikePost(@PathVariable("postId") Long postId, Principal principal) {
        likeService.unlikePost(principal.getName(), postId);
        return new LikeResponseDto("게시물 좋아요 취소");
    }

    @PostMapping("/post/{postId}/comment/{commentId}/like")
    public LikeResponseDto likeComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, Principal principal) {
        likeService.likeComment(principal.getName(), postId, commentId);
        return new LikeResponseDto("댓글 좋아요 성공");
    }

    @DeleteMapping("/post/{postId}/comment/{commentId}/unlike")
    public LikeResponseDto unlikeComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, Principal principal) {
        likeService.unlikeComment(principal.getName(), postId, commentId);
        return new LikeResponseDto("댓글 좋아요 취소");
    }
}
