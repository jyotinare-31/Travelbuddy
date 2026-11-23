package com.smarttourism.smart_tourism_backend.dto;

public class CreateReviewRequest {
    private Integer rating;
    private String reviewText;
    private String visitDate;

    public CreateReviewRequest() {
    }

    public CreateReviewRequest(Integer rating, String reviewText, String visitDate) {
        this.rating = rating;
        this.reviewText = reviewText;
        this.visitDate = visitDate;
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
}

