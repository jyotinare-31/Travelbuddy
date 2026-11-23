package com.smarttourism.smart_tourism_backend.dto;

public class RouteStopDto {

    private Integer placeId;
    private String placeName;
    private String location;
    private String category;
    private String subCategory;
    private Double latitude;
    private Double longitude;
    private int stopOrder;
    private boolean isStart;
    private boolean isFinal;
    private double distanceFromPreviousKm;
    private double durationFromPreviousMinutes;
    private String imageUrl;

    public RouteStopDto() {
    }

    public RouteStopDto(
            Integer placeId,
            String placeName,
            String location,
            String category,
            String subCategory,
            Double latitude,
            Double longitude,
            int stopOrder,
            boolean isStart,
            boolean isFinal,
            double distanceFromPreviousKm,
            double durationFromPreviousMinutes,
            String imageUrl) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.location = location;
        this.category = category;
        this.subCategory = subCategory;
        this.latitude = latitude;
        this.longitude = longitude;
        this.stopOrder = stopOrder;
        this.isStart = isStart;
        this.isFinal = isFinal;
        this.distanceFromPreviousKm = distanceFromPreviousKm;
        this.durationFromPreviousMinutes = durationFromPreviousMinutes;
        this.imageUrl = imageUrl;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public double getDistanceFromPreviousKm() {
        return distanceFromPreviousKm;
    }

    public void setDistanceFromPreviousKm(double distanceFromPreviousKm) {
        this.distanceFromPreviousKm = distanceFromPreviousKm;
    }

    public double getDurationFromPreviousMinutes() {
        return durationFromPreviousMinutes;
    }

    public void setDurationFromPreviousMinutes(double durationFromPreviousMinutes) {
        this.durationFromPreviousMinutes = durationFromPreviousMinutes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

