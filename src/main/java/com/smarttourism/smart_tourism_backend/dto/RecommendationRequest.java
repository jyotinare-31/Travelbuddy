package com.smarttourism.smart_tourism_backend.dto;

import java.util.List;

public class RecommendationRequest {

    private String location;

    private double budget;

    private int days;

    private String travelPreference;

    private List<String> interests;

    private String preferredCategory;

    private Double minimumRating;


    // Default constructor
    public RecommendationRequest() {
    }


    // Getters and Setters

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }


    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }


    public String getTravelPreference() {
        return travelPreference;
    }

    public void setTravelPreference(String travelPreference) {
        this.travelPreference = travelPreference;
    }


    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public String getPreferredCategory() {
        return preferredCategory;
    }

    public void setPreferredCategory(String preferredCategory) {
        this.preferredCategory = preferredCategory;
    }

    public Double getMinimumRating() {
        return minimumRating;
    }

    public void setMinimumRating(Double minimumRating) {
        this.minimumRating = minimumRating;
    }

    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}