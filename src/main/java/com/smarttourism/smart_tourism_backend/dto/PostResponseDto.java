package com.smarttourism.smart_tourism_backend.dto;

import com.smarttourism.smart_tourism_backend.entity.CommunityPost;
import java.time.LocalDateTime;

public class PostResponseDto {
    private Integer postId;
    private String title;
    private String description;
    private Integer destinationId;
    private String destinationName;
    private Integer rating;
    private String imageUrl;
    private Integer likesCount;
    private Integer commentsCount;
    private Boolean isLikedByCurrentUser;
    private Boolean isOwner;
    private UserDto author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostResponseDto() {
    }

    public static PostResponseDto fromEntity(CommunityPost post, Integer currentUserId, boolean isLiked) {
        if (post == null) {
            return null;
        }
        PostResponseDto dto = new PostResponseDto();
        dto.setPostId(post.getPostId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setDestinationId(post.getDestinationId());
        dto.setDestinationName(post.getDestinationName());
        dto.setRating(post.getRating());
        dto.setImageUrl(post.getImageUrl());
        dto.setLikesCount(post.getLikesCount());
        dto.setCommentsCount(post.getCommentsCount());
        dto.setIsLikedByCurrentUser(isLiked);
        dto.setIsOwner(currentUserId != null && post.getUser() != null && currentUserId.equals(post.getUser().getUserId()));
        dto.setAuthor(UserDto.fromEntity(post.getUser()));
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        return dto;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Boolean getIsLikedByCurrentUser() {
        return isLikedByCurrentUser;
    }

    public void setIsLikedByCurrentUser(Boolean likedByCurrentUser) {
        isLikedByCurrentUser = likedByCurrentUser;
    }

    public Boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Boolean owner) {
        isOwner = owner;
    }

    public UserDto getAuthor() {
        return author;
    }

    public void setAuthor(UserDto author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

