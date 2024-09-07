package com.eoboard.repository.post.query;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostQueryDto {

    private Long postId;
    private String topic;
    private String title;
    private String content;
    private Long memberId;
    private String nickName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<PostCommentQueryDto> postComments;

    public PostQueryDto(Long postId, String topic, String title, String content, Long memberId, String nickName, LocalDateTime createdAt, LocalDateTime updatedAt, List<PostCommentQueryDto> postComments) {
        this.postId = postId;
        this.topic = topic;
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.nickName = nickName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.postComments = postComments;
    }

    public PostQueryDto(Long postId, String topic, String title, String content, Long memberId, String nickName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postId = postId;
        this.topic = topic;
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.nickName = nickName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
