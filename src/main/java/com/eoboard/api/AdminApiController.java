package com.eoboard.api;

import com.eoboard.dto.comment.CommentResponseDto;
import com.eoboard.dto.member.MemberDto;
import com.eoboard.dto.member.MemberRequestDto;
import com.eoboard.dto.member.MemberResponseDto;
import com.eoboard.dto.post.PostDto;
import com.eoboard.dto.post.PostResponseDto;
import com.eoboard.repository.MemberRepository;
import com.eoboard.repository.PostRepository;
import com.eoboard.service.CommentService;
import com.eoboard.service.MemberService;
import com.eoboard.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class AdminApiController {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    /**
     * 멤버 전체 조회
     */
    @GetMapping("/admin/members")
    public Page<MemberDto> getMembers(Pageable pageable) {
        return memberRepository.findAllMembers(pageable);
    }

    /**
     * 멤버 수정
     */
    @PutMapping("/admin/members/{memberId}")
    public MemberResponseDto updateMember(@PathVariable("memberId") Long memberId, @RequestBody MemberRequestDto request) {
        memberService.updateMember(memberId, request);
        return new MemberResponseDto(memberId);
    }

    /**
     * 멤버 삭제
     */
    @DeleteMapping("/admin/members/{memberId}")
    public MemberResponseDto deleteMember(@PathVariable("memberId") Long memberId) {
        memberService.deleteMember(memberId);
        return new MemberResponseDto(memberId);
    }

    /**
     * 게시물 삭제
     */
    @DeleteMapping("/admin/post/{postId}")
    public PostResponseDto deletePost(@PathVariable("postId") Long postId) {
        postService.deletePost(postId);
        return new PostResponseDto(postId);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/admin/post/{postId}/comment/{commentId}")
    public CommentResponseDto deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        PostDto post = postRepository.findPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
        }

        commentService.deleteComment(commentId);
        return new CommentResponseDto(commentId);

    }
}
