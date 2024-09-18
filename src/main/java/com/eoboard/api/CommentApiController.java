package com.eoboard.api;

import com.eoboard.dto.comment.PostCommentDto;
import com.eoboard.dto.post.PostDto;
import com.eoboard.repository.CommentRepository;
import com.eoboard.repository.PostRepository;
import com.eoboard.service.CommentService;
import com.eoboard.service.MemberService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentApiController {

    private final MemberService memberService;
    private final CommentService commentService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;


    @PostMapping("/api/v1/post/{postId}/comment")
    public CommentResponse createParentComment(@PathVariable("postId") Long postId, @RequestBody @Valid CommentRequest request, Principal principal) {
        PostDto post = postRepository.findPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
        }

        String memberId = principal.getName();

        Long commentId = commentService.createComment(memberId, postId, request.content, null);
        return new CommentResponse(commentId);
    }

    @PostMapping("/api/v1/post/{postId}/comment/{parentId}")
    public CommentResponse createChildComment(@PathVariable("postId") Long postId, @PathVariable("parentId") Long parentId,
                                         @RequestBody @Valid CommentRequest request, Principal principal) {
        PostDto post = postRepository.findPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
        }

        String memberId = principal.getName();

        Long commentId = commentService.createComment(memberId, postId, request.content, parentId);
        return new CommentResponse(commentId);
    }

    @GetMapping("/api/v1/post/{postId}/comment")
    public List<PostCommentDto> getComments(@PathVariable("postId") Long postId) {
        PostDto post = postRepository.findPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
        }

        return commentRepository.findCommentsByPostId(postId);
    }

    @PutMapping("/api/v1/post/{postId}/comment/{commentId}")
    public CommentResponse updateComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId
            , @RequestBody @Valid CommentRequest request, Principal principal) {

        PostDto post = postRepository.findPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
        }

        Long postMemberId = post.getMemberId();
        Long memberId = memberService.findOneByMemberId(principal.getName()).getId();
        if (memberId != postMemberId) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        commentService.updateComment(commentId, request.content);
        return new CommentResponse(commentId);
    }

    @DeleteMapping("/api/v1/post/{postId}/comment/{commentId}")
    public CommentResponse deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, Principal principal) {
        PostDto post = postRepository.findPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
        }

        Long postMemberId = post.getMemberId();
        Long memberId = memberService.findOneByMemberId(principal.getName()).getId();
        if (memberId != postMemberId) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        commentService.deleteComment(commentId);
        return new CommentResponse(commentId);
    }

    @Data
    static class CommentRequest {
        private String content;
    }

    @Data
    static class CommentResponse {
        private Long commentId;

        public CommentResponse(Long commentId) {
            this.commentId = commentId;
        }
    }



}
