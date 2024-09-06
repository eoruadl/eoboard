package com.eoboard.api;

import com.eoboard.domain.Post;
import com.eoboard.service.MemberService;
import com.eoboard.service.PostService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostApiController {

    private final MemberService memberService;
    private final PostService postService;

    @PostMapping("/api/v1/post")
    public CreatePostResponse createPost(@RequestBody @Valid CreatePostRequest request, Principal principal) {

        Long memberId = memberService.findOneByMemberId(principal.getName()).getId();

        Long postId = postService.post(memberId, request.topic, request.title, request.content);

        return new CreatePostResponse(postId);
    }

    @GetMapping("/api/v1/post")
    public List<Post> getAllPost() {
        List<Post> allPost = postService.findAllPost();
        return allPost;
    }

    @Data
    static class CreatePostRequest {
        private String topic;
        private String title;
        private String content;

    }

    @Data
    static class CreatePostResponse {
        private Long post_id;

        public CreatePostResponse(Long post_id) {
            this.post_id = post_id;
        }
    }



}
