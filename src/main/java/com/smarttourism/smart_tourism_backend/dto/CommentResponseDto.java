package com.smarttourism.smart_tourism_backend.dto;

import com.smarttourism.smart_tourism_backend.entity.PostComment;
import java.time.LocalDateTime;

public class CommentResponseDto {
    private Integer commentId;
    private Integer postId;
    private String commentText;
    private UserDto author;
    private Boolean isOwner;
    private LocalDateTime createdAt;

    public CommentResponseDto() {
    }

    public static CommentResponseDto fromEntity(PostComment comment, Integer currentUserId) {
        if (comment == null) {
            return null;
        }
        CommentResponseDto dto = new CommentResponseDto();
        dto.setCommentId(comment.getCommentId());
        dto.setPostId(comment.getPost() != null ? comment.getPost().getPostId() : null);
        dto.setCommentText(comment.getCommentText());
        dto.setAuthor(UserDto.fromEntity(comment.getUser()));
        dto.setIsOwner(currentUserId != null && comment.getUser() != null && currentUserId.equals(comment.getUser().getUserId()));
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public UserDto getAuthor() {
        return author;
    }

    public void setAuthor(UserDto author) {
        this.author = author;
    }

    public Boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Boolean owner) {
        isOwner = owner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

