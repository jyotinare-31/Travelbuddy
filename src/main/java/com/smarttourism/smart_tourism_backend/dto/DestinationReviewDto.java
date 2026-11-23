package com.smarttourism.smart_tourism_backend.dto;

import com.smarttourism.smart_tourism_backend.entity.DestinationReview;
import java.time.LocalDateTime;

public class DestinationReviewDto {
    private Integer reviewId;
    private Integer destinationId;
    private Integer rating;
    private String reviewText;
    private String visitDate;
    private UserDto author;
    private Boolean isOwner;
    private LocalDateTime createdAt;

    public DestinationReviewDto() {
    }

    public static DestinationReviewDto fromEntity(DestinationReview review, Integer currentUserId) {
        if (review == null) {
            return null;
        }
        DestinationReviewDto dto = new DestinationReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setDestinationId(review.getDestinationId());
        dto.setRating(review.getRating());
        dto.setReviewText(review.getReviewText());
        dto.setVisitDate(review.getVisitDate());
        dto.setAuthor(UserDto.fromEntity(review.getUser()));
        dto.setIsOwner(currentUserId != null && review.getUser() != null && currentUserId.equals(review.getUser().getUserId()));
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
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

