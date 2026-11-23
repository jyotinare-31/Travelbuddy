package com.smarttourism.smart_tourism_backend.entity;

public class BudgetEstimate {

    private Integer placeId;
    private String destination;
    private int days;
    private int travelers;

    private double transportationCost;
    private double accommodationCost;
    private double foodCost;

    private double totalBudget;
    private double perPerson;

    private TransportationDetail transportation;
    private AccommodationDetail accommodation;
    private FoodDetail food;

    public BudgetEstimate() {
    }

    public BudgetEstimate(
            Integer placeId,
            String destination,
            int days,
            int travelers,
            double transportationCost,
            double accommodationCost,
            double foodCost,
            double totalBudget,
            double perPerson,
            TransportationDetail transportation,
            AccommodationDetail accommodation,
            FoodDetail food) {
        this.placeId = placeId;
        this.destination = destination;
        this.days = days;
        this.travelers = travelers;
        this.transportationCost = transportationCost;
        this.accommodationCost = accommodationCost;
        this.foodCost = foodCost;
        this.totalBudget = totalBudget;
        this.perPerson = perPerson;
        this.transportation = transportation;
        this.accommodation = accommodation;
        this.food = food;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer placeId) {
        this.placeId = placeId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getTravelers() {
        return travelers;
    }

    public void setTravelers(int travelers) {
        this.travelers = travelers;
    }

    public double getTransportationCost() {
        return transportationCost;
    }

    public void setTransportationCost(double transportationCost) {
        this.transportationCost = transportationCost;
    }

    public double getAccommodationCost() {
        return accommodationCost;
    }

    public void setAccommodationCost(double accommodationCost) {
        this.accommodationCost = accommodationCost;
    }

    public double getFoodCost() {
        return foodCost;
    }

    public void setFoodCost(double foodCost) {
        this.foodCost = foodCost;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public double getPerPerson() {
        return perPerson;
    }

    public void setPerPerson(double perPerson) {
        this.perPerson = perPerson;
    }

    public TransportationDetail getTransportation() {
        return transportation;
    }

    public void setTransportation(TransportationDetail transportation) {
        this.transportation = transportation;
    }

    public AccommodationDetail getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(AccommodationDetail accommodation) {
        this.accommodation = accommodation;
    }

    public FoodDetail getFood() {
        return food;
    }

    public void setFood(FoodDetail food) {
        this.food = food;
    }

    // ==========================================
    // DETAIL DTO CLASSES
    // ==========================================

    public static class TransportationDetail {
        private String type;
        private Double baseFare;
        private Double farePerKm;
        private Double estimatedDistanceKm;
        private double estimatedCost;

        public TransportationDetail() {
        }

        public TransportationDetail(String type, Double baseFare, Double farePerKm, Double estimatedDistanceKm, double estimatedCost) {
            this.type = type;
            this.baseFare = baseFare;
            this.farePerKm = farePerKm;
            this.estimatedDistanceKm = estimatedDistanceKm;
            this.estimatedCost = estimatedCost;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Double getBaseFare() {
            return baseFare;
        }

        public void setBaseFare(Double baseFare) {
            this.baseFare = baseFare;
        }

        public Double getFarePerKm() {
            return farePerKm;
        }

        public void setFarePerKm(Double farePerKm) {
            this.farePerKm = farePerKm;
        }

        public Double getEstimatedDistanceKm() {
            return estimatedDistanceKm;
        }

        public void setEstimatedDistanceKm(Double estimatedDistanceKm) {
            this.estimatedDistanceKm = estimatedDistanceKm;
        }

        public double getEstimatedCost() {
            return estimatedCost;
        }

        public void setEstimatedCost(double estimatedCost) {
            this.estimatedCost = estimatedCost;
        }
    }

    public static class AccommodationDetail {
        private String hotelName;
        private double pricePerNight;
        private int nights;
        private double totalCost;

        public AccommodationDetail() {
        }

        public AccommodationDetail(String hotelName, double pricePerNight, int nights, double totalCost) {
            this.hotelName = hotelName;
            this.pricePerNight = pricePerNight;
            this.nights = nights;
            this.totalCost = totalCost;
        }

        public String getHotelName() {
            return hotelName;
        }

        public void setHotelName(String hotelName) {
            this.hotelName = hotelName;
        }

        public double getPricePerNight() {
            return pricePerNight;
        }

        public void setPricePerNight(double pricePerNight) {
            this.pricePerNight = pricePerNight;
        }

        public int getNights() {
            return nights;
        }

        public void setNights(int nights) {
            this.nights = nights;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }
    }

    public static class FoodDetail {
        private String restaurantName;
        private double estimatedPerMeal;
        private int mealsPerDay;
        private double totalCost;

        public FoodDetail() {
        }

        public FoodDetail(String restaurantName, double estimatedPerMeal, int mealsPerDay, double totalCost) {
            this.restaurantName = restaurantName;
            this.estimatedPerMeal = estimatedPerMeal;
            this.mealsPerDay = mealsPerDay;
            this.totalCost = totalCost;
        }

        public String getRestaurantName() {
            return restaurantName;
        }

        public void setRestaurantName(String restaurantName) {
            this.restaurantName = restaurantName;
        }

        public double getEstimatedPerMeal() {
            return estimatedPerMeal;
        }

        public void setEstimatedPerMeal(double estimatedPerMeal) {
            this.estimatedPerMeal = estimatedPerMeal;
        }

        public int getMealsPerDay() {
            return mealsPerDay;
        }

        public void setMealsPerDay(int mealsPerDay) {
            this.mealsPerDay = mealsPerDay;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }
    }
}