package com.eoboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String topic;
    private String title;
    private String content;
    private int likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostLike> postLikes = new ArrayList<>();

    public static Post createPost(Member member, String topic, String title, String content) {
        Post post = new Post();
        post.setMember(member);
        post.setTopic(topic);
        post.setTitle(title);
        post.setContent(content);
        post.updateCreatedAt();
        return post;
    }

    public void upLike() {
        this.likeCount++;
    }

    public void downLike() {
        this.likeCount--;
    }
}
