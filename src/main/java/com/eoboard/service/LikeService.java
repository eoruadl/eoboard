package com.eoboard.service;

import com.eoboard.domain.*;
import com.eoboard.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public Long likePost(String memberId, Long postId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(NoSuchFieldError::new);
        Post post = postRepository.findById(postId).orElseThrow(NoClassDefFoundError::new);

        Optional<PostLike> findLike = postLikeRepository.findByMemberAndPost(member, post);
        if (findLike.isEmpty()) {
            PostLike postLike = PostLike.builder().member(member).post(post).build();
            postLikeRepository.save(postLike);
            post.upLike();
            return postLike.getId();
        } else {
            return findLike.get().getId();
        }
    }

    @Transactional
    public void unlikePost(String memberId, Long postId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(NoSuchFieldError::new);
        Post post = postRepository.findById(postId).orElseThrow(NoSuchFieldError::new);

        PostLike postLike = postLikeRepository.findByMemberAndPost(member, post).orElseThrow(NoSuchFieldError::new);

        postLikeRepository.delete(postLike);
        post.downLike();
    }

    @Transactional
    public Long likeComment(String memberId, Long commentId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(NoSuchFieldError::new);
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchFieldError::new);

        Optional<CommentLike> findComment = commentLikeRepository.findByMemberAndComment(member, comment);
        if (findComment.isEmpty()) {
            CommentLike commentLike = CommentLike.builder().member(member).comment(comment).build();
            commentLikeRepository.save(commentLike);
            comment.upLike();
            return commentLike.getId();
        } else {
            return findComment.get().getId();
        }
    }

    @Transactional
    public void unlikeComment(String memberId, Long commentId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(NoSuchFieldError::new);
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchFieldError::new);

        CommentLike commentLike = commentLikeRepository.findByMemberAndComment(member, comment).orElseThrow(NoSuchFieldError::new);

        commentLikeRepository.delete(commentLike);
        comment.downLike();
    }
}
