package com.eoboard.api;

import com.eoboard.dto.post.PostDto;
import com.eoboard.dto.post.PostPageDto;
import com.eoboard.dto.post.PostRequestDto;
import com.eoboard.dto.post.PostResponseDto;
import com.eoboard.repository.PostRepository;
import com.eoboard.service.MemberService;
import com.eoboard.service.PostService;
import jakarta.validation.Valid;
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
    public PostResponseDto createPost(@RequestBody @Valid PostRequestDto request, Principal principal) {

        Long memberId = memberService.findOneByMemberId(principal.getName()).getId();

        Long postId = postService.post(memberId, request.getTopic(), request.getTitle(), request.getContent());

        return new PostResponseDto(postId);
    }

    @GetMapping("/api/v1/post")
    public Page<PostPageDto> getPosts(Pageable pageable) {
        return postRepository.findPagePost(pageable);
    }

    @GetMapping("/api/v1/post/{postId}")
    public PostDto getPost(@PathVariable("postId") Long postId) {
        PostDto post = postRepository.findPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
        }
        return post;
    }

    @PutMapping("/api/v1/post/{postId}")
    public PostResponseDto updatePost(
            @PathVariable("postId") Long postId,
            @RequestBody @Valid PostRequestDto request, Principal principal) {
        Long memberId = memberService.findOneByMemberId(principal.getName()).getId();
        PostDto post = postRepository.findPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
        }

        Long postMemberId = post.getMemberId();
        if (memberId != postMemberId) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        postService.updatePost(postId, request.getTopic(), request.getTitle(), request.getContent());
        return new PostResponseDto(postId);
    }

    @DeleteMapping("/api/v1/post/{postId}")
    public PostResponseDto deletePost(@PathVariable("postId") Long postId, Principal principal) {
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
        return new PostResponseDto(postId);
    }
}
