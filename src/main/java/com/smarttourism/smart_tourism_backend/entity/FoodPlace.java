package com.smarttourism.smart_tourism_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "food_places")
public class FoodPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Integer foodId;

    @Column(name = "place_id", nullable = false)
    private Integer placeId;

    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;

    @Column(name = "category")
    private String category;

    @Column(name = "price_per_meal", nullable = false)
    private Double pricePerMeal;

    @Column(name = "rating")
    private Double rating;

    public FoodPlace() {
    }

    public FoodPlace(Integer placeId, String restaurantName, String category, Double pricePerMeal, Double rating) {
        this.placeId = placeId;
        this.restaurantName = restaurantName;
        this.category = category;
        this.pricePerMeal = pricePerMeal;
        this.rating = rating;
    }

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer placeId) {
        this.placeId = placeId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPricePerMeal() {
        return pricePerMeal;
    }

    public void setPricePerMeal(Double pricePerMeal) {
        this.pricePerMeal = pricePerMeal;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}

