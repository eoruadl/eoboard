package com.eoboard.service;

import com.eoboard.domain.Member;
import com.eoboard.domain.Post;
import com.eoboard.repository.MemberRepository;
import com.eoboard.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    /**
     * 게시물 작성
     */
    @Transactional
    public Long post(Long memberId, String topic, String title, String content) {
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);

        Post post = Post.createPost(member, topic, title, content);

        postRepository.save(post);

        return post.getId();
    }

    /**
     * 게시물 수정
     */
    @Transactional
    public void updatePost(Long postId, String topic, String title, String content) {
        Post findPost = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);
        findPost.setTopic(topic);
        findPost.setTitle(title);
        findPost.setContent(content);
        findPost.updateCreatedAt();
    }

    /**
     * 게시물 삭제
     */
    @Transactional
    public void deletePost(Long postId) {
        Post findPost = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);
        postRepository.delete(findPost);
    }

    /**
     * 게시물 전체 조회
     */
    public List<Post> findAllPost() {
        return postRepository.findAll();
    }

}
