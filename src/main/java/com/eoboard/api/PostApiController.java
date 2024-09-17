package com.eoboard.api;

import com.eoboard.dto.post.PostDto;
import com.eoboard.dto.post.PostPageDto;
import com.eoboard.repository.PostRepository;
import com.eoboard.service.MemberService;
import com.eoboard.service.PostService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class PostApiController {

    private final MemberService memberService;
    private final PostService postService;
    private final PostRepository postRepository;

    @PostMapping("/api/v1/post")
    public PostResponse createPost(@RequestBody @Valid PostRequest request, Principal principal) {

        Long memberId = memberService.findOneByMemberId(principal.getName()).getId();

        Long postId = postService.post(memberId, request.topic, request.title, request.content);

        return new PostResponse(postId);
    }

    @GetMapping("/api/v1/post")
    public Page<PostPageDto> getPosts(Pageable pageable) {
        return postRepository.findPagePost(pageable);
    }

    @GetMapping("/api/v1/post/{postId}")
    public PostDto getPost(@PathVariable("postId") Long postId) {
        PostDto post = postRepository.findPost(postId);
        System.out.println(post);
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
        PostDto post = postRepository.findPost(postId);
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
        PostDto post = postRepository.findPost(postId);
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
