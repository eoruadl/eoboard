package com.eoboard.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    private String content;
    private Long parentId;
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
