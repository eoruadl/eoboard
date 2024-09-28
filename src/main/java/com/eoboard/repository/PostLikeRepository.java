package com.eoboard.repository;

import com.eoboard.domain.Member;
import com.eoboard.domain.Post;
import com.eoboard.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByMemberAndPost(Member member, Post post);
}
