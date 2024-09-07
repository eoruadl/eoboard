package com.eoboard.service;

import com.eoboard.domain.Comment;
import com.eoboard.domain.Member;
import com.eoboard.domain.Post;
import com.eoboard.repository.CommentRepository;
import com.eoboard.repository.MemberRepository;
import com.eoboard.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /**
     * 댓글 작성
     */
    @Transactional
    public Long comment(String memberId, Long postId, String content) {
        Member member = memberRepository.findByMemberId(memberId).get(0);
        Post post = postRepository.findOne(postId);

        Comment comment = Comment.createComment(member, post, content);

        commentRepository.save(comment);

        return comment.getId();
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public void updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findOne(commentId);
        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findOne(commentId);
        commentRepository.delete(comment);
    }
}
