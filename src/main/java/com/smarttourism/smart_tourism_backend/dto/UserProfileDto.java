package com.smarttourism.smart_tourism_backend.dto;

import java.util.List;

public class UserProfileDto {
    private UserDto user;
    private int postsCount;
    private int reviewsCount;
    private int totalLikesReceived;
    private List<PostResponseDto> posts;
    private List<DestinationReviewDto> reviews;

    public UserProfileDto() {
    }

    public UserProfileDto(UserDto user, int postsCount, int reviewsCount, int totalLikesReceived,
                          List<PostResponseDto> posts, List<DestinationReviewDto> reviews) {
        this.user = user;
        this.postsCount = postsCount;
        this.reviewsCount = reviewsCount;
        this.totalLikesReceived = totalLikesReceived;
        this.posts = posts;
        this.reviews = reviews;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public int getTotalLikesReceived() {
        return totalLikesReceived;
    }

    public void setTotalLikesReceived(int totalLikesReceived) {
        this.totalLikesReceived = totalLikesReceived;
    }

    public List<PostResponseDto> getPosts() {
        return posts;
    }

    public void setPosts(List<PostResponseDto> posts) {
        this.posts = posts;
    }

    public List<DestinationReviewDto> getReviews() {
        return reviews;
    }

    public void setReviews(List<DestinationReviewDto> reviews) {
        this.reviews = reviews;
    }
}

