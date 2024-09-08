package com.eoboard.api;

import com.eoboard.repository.post.query.PostQueryDto;
import com.eoboard.repository.post.query.PostQueryRepository;
import com.eoboard.service.MemberService;
import com.eoboard.service.PostService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostApiController {

    private final MemberService memberService;
    private final PostService postService;
    private final PostQueryRepository postQueryRepository;

    @PostMapping("/api/v1/post")
    public PostResponse createPost(@RequestBody @Valid PostRequest request, Principal principal) {

        Long memberId = memberService.findOneByMemberId(principal.getName()).getId();

        Long postId = postService.post(memberId, request.topic, request.title, request.content);

        return new PostResponse(postId);
    }

    @GetMapping("/api/v1/post")
    public List<PostQueryDto> getPosts(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return postQueryRepository.findAllPost(offset, limit);
    }

    @GetMapping("/api/v1/post/{postId}")
    public PostQueryDto getPost(@PathVariable("postId") Long postId) {
        PostQueryDto post = postQueryRepository.findPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
        }
        return post;
    }

    @PutMapping("/api/v1/post/{postId}")
    public PostResponse updatePost(
            @PathVariable("postId") Long postId,
            @RequestBody @Valid PostRequest request, Principal principal) {
        Long memberId = memberService.findOneByMemberId(principal.getName()).getId();
        PostQueryDto post = postQueryRepository.findPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
        }

        Long postMemberId = post.getMemberId();
        if (memberId != postMemberId) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        postService.updatePost(postId, request.topic, request.title, request.content);
        return new PostResponse(postId);
    }

    @DeleteMapping("/api/v1/post/{postId}")
    public PostResponse deletePost(@PathVariable("postId") Long postId, Principal principal) {
        Long memberId = memberService.findOneByMemberId(principal.getName()).getId();
        PostQueryDto post = postQueryRepository.findPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
        }

        Long postMemberId = post.getMemberId();
        if (memberId != postMemberId) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        postService.deletePost(postId);
        return new PostResponse(postId);
    }

    @Data
    static class PostRequest {
        private String topic;
        private String title;
        private String content;

    }

    @Data
    static class PostResponse {
        private Long post_id;

        public PostResponse(Long post_id) {
            this.post_id = post_id;
        }
    }

    @Data
    static class UpdatePostResponse {

        private Long post_id;
        private String topic;
        private String title;
        private String content;

        public UpdatePostResponse(Long post_id, String topic, String title, String content) {
            this.post_id = post_id;
            this.topic = topic;
            this.title = title;
            this.content = content;
        }
    }




}
