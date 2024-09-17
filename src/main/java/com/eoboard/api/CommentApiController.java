package com.eoboard.api;

import com.eoboard.repository.post.query.PostCommentQueryDto;
import com.eoboard.repository.post.query.PostQueryDto;
import com.eoboard.repository.post.query.PostQueryRepository;
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
    private final PostQueryRepository postQueryRepository;


    @PostMapping("/api/v1/post/{postId}/comment")
    public CommentResponse createComment(@PathVariable("postId") Long postId, @RequestBody @Valid CommentRequest request, Principal principal) {
        PostQueryDto post = postQueryRepository.findPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
        }

        String memberId = principal.getName();

        Long commentId = commentService.comment(memberId, postId, request.content);
        return new CommentResponse(commentId);
    }

    @GetMapping("/api/v1/post/{postId}/comment")
    public List<PostCommentQueryDto> getComments(@PathVariable("postId") Long postId) {
        PostQueryDto post = postQueryRepository.findPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
        }

        return postQueryRepository.findComments(postId);
    }

    @PutMapping("/api/v1/post/{postId}/comment/{commentId}")
    public CommentResponse updateComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId
            , @RequestBody @Valid CommentRequest request, Principal principal) {

        PostQueryDto post = postQueryRepository.findPost(postId);
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
        PostQueryDto post = postQueryRepository.findPost(postId);
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
