package com.smarttourism.smart_tourism_backend.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "tourist_places")
public class TouristPlace {

    @Id
    @Column(name = "place_id")
    private Integer placeId;

    @Column(name = "place_name")
    private String placeName;

    @Column(name = "location")
    private String location;

    @Column(name = "category")
    private String category;

    @Column(name = "sub_category")
    private String subCategory;

    @Column(name = "description")
    private String description;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "image_url")
    private String imageUrl;

    // =====================================================
    // LATITUDE
    // =====================================================

    @Column(name = "latitude")
    private Double latitude;

    // =====================================================
    // LONGITUDE
    // =====================================================

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "ticket_price")
    private Double ticketPrice;

    // =====================================================
    // HIDDEN DESTINATION
    // =====================================================

    @Column(name = "is_hidden")
    private Boolean isHidden;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public TouristPlace() {
    }


    // =====================================================
    // PLACE ID
    // =====================================================

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer placeId) {
        this.placeId = placeId;
    }


    // =====================================================
    // PLACE NAME
    // =====================================================

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }


    // =====================================================
    // LOCATION
    // =====================================================

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    // =====================================================
    // CATEGORY
    // =====================================================

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    // =====================================================
    // SUB CATEGORY
    // =====================================================

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }


    // =====================================================
    // DESCRIPTION
    // =====================================================

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    // =====================================================
    // RATING
    // =====================================================

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }


    // =====================================================
    // REVIEW COUNT
    // =====================================================

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }


    // =====================================================
    // OPENING TIME
    // =====================================================

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }


    // =====================================================
    // CLOSING TIME
    // =====================================================

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }


    // =====================================================
    // IMAGE URL
    // =====================================================

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    // =====================================================
    // LATITUDE
    // =====================================================

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }


    // =====================================================
    // LONGITUDE
    // =====================================================

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    public Double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(Double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    // =====================================================
    // HIDDEN DESTINATION
    // =====================================================

    public Boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }
}