package com.eoboard.service;

import com.eoboard.domain.Comment;
import com.eoboard.domain.Member;
import com.eoboard.domain.Post;
import com.eoboard.dto.comment.PostCommentDto;
import com.eoboard.repository.CommentRepository;
import com.eoboard.repository.MemberRepository;
import com.eoboard.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    public Long createComment(String memberId, Long postId, String content, Long parentId) {
        Member findMember = memberRepository.findByMemberId(memberId).orElseThrow(NoSuchElementException::new);
        Post findPost = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);

        Comment comment = new Comment(content, findMember, findPost);
        Comment parentComment;
        if (parentId != null) {
            parentComment = commentRepository.findById(parentId).orElseThrow(NoSuchElementException::new);
            comment.updateParent(parentComment);
        }
        comment.updateCreatedAt();

        commentRepository.save(comment);
        return comment.getId();
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public void updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        comment.updateContent(content);
        comment.updateUpdatedAt();
    }

    /**
     * 댓글 삭제
     */

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findCommentByIdWithParent(commentId).orElseThrow(NoSuchElementException::new);
        System.out.println(comment.getChildren());

        if (comment.getChildren().size() != 0) {
            comment.updateIsDeleted(true);
            comment.updateDeletedAt();
        } else {
            commentRepository.delete(getDeletableAncestorComment(comment));
        }
    }

    private Comment getDeletableAncestorComment(Comment comment) {
        Comment parent = comment.getParent();
        if (parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted()) {
            return getDeletableAncestorComment(parent);
        }
        return comment;
    }
}
