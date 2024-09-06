package com.eoboard.service;

import com.eoboard.domain.Member;
import com.eoboard.domain.Post;
import com.eoboard.repository.MemberRepository;
import com.eoboard.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        Member member = memberRepository.findOne(memberId);

        Post post = Post.createPost(member, topic, title, content);

        postRepository.save(post);

        return post.getId();
    }

    /**
     * 게시물 수정
     */
    @Transactional
    public void updatePost(Long post_id, String topic, String title, String content) {
        Post findPost = postRepository.findOne(post_id);
        findPost.setTopic(topic);
        findPost.setTitle(title);
        findPost.setContent(content);
        findPost.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 게시물 전체 조회
     */
    public List<Post> findAllPost() {
        return postRepository.findAll();
    }
}
