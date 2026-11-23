package com.smarttourism.smart_tourism_backend.service;

import com.smarttourism.smart_tourism_backend.entity.BudgetEstimate;
import com.smarttourism.smart_tourism_backend.entity.FoodPlace;
import com.smarttourism.smart_tourism_backend.entity.Hotel;
import com.smarttourism.smart_tourism_backend.entity.TouristPlace;
import com.smarttourism.smart_tourism_backend.entity.Transportation;
import com.smarttourism.smart_tourism_backend.repository.FoodPlaceRepository;
import com.smarttourism.smart_tourism_backend.repository.HotelRepository;
import com.smarttourism.smart_tourism_backend.repository.TouristPlaceRepository;
import com.smarttourism.smart_tourism_backend.repository.TransportationRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BudgetService {

    private final TouristPlaceRepository touristPlaceRepository;
    private final TransportationRepository transportationRepository;
    private final HotelRepository hotelRepository;
    private final FoodPlaceRepository foodPlaceRepository;

    public BudgetService(
            TouristPlaceRepository touristPlaceRepository,
            TransportationRepository transportationRepository,
            HotelRepository hotelRepository,
            FoodPlaceRepository foodPlaceRepository) {
        this.touristPlaceRepository = touristPlaceRepository;
        this.transportationRepository = transportationRepository;
        this.hotelRepository = hotelRepository;
        this.foodPlaceRepository = foodPlaceRepository;
    }

    public BudgetEstimate calculateBudget(Integer placeId, int days, int travelers) {
        return calculateBudget(placeId, days, travelers, null);
    }

    public BudgetEstimate calculateBudget(Integer placeId, int days, int travelers, String transportType) {
        if (placeId == null || placeId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Place ID must be a valid positive integer");
        }

        if (days <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip duration (days) must be at least 1");
        }

        if (travelers <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number of travelers must be at least 1");
        }

        TouristPlace place = touristPlaceRepository.findById(placeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tourist place not found with ID: " + placeId));

        // 1. Transportation Calculation
        List<Transportation> transportList = transportationRepository.findByPlaceId(placeId);
        Transportation selectedTransport = null;
        if (!transportList.isEmpty()) {
            if (transportType != null && !transportType.isBlank()) {
                selectedTransport = transportList.stream()
                        .filter(t -> t.getTransportType().equalsIgnoreCase(transportType.trim()))
                        .findFirst()
                        .orElse(null);
            }
            if (selectedTransport == null) {
                selectedTransport = transportList.stream()
                        .filter(t -> "Auto".equalsIgnoreCase(t.getTransportType()))
                        .findFirst()
                        .orElse(transportList.get(0));
            }
        } else {
            selectedTransport = new Transportation(placeId, "Auto", 30.0, 15.0);
        }

        double estimatedDistanceKm = estimateDistanceKm(place);
        // Formula: transport cost = base_fare + (estimated distance * fare_per_km)
        double transportationCost = round2(selectedTransport.getBaseFare() + (estimatedDistanceKm * selectedTransport.getFarePerKm()));

        BudgetEstimate.TransportationDetail transportDetail = new BudgetEstimate.TransportationDetail(
                selectedTransport.getTransportType(),
                selectedTransport.getBaseFare(),
                selectedTransport.getFarePerKm(),
                round2(estimatedDistanceKm),
                transportationCost
        );

        // 2. Accommodation Calculation
        List<Hotel> hotelList = hotelRepository.findByPlaceId(placeId);
        Hotel selectedHotel = null;
        if (!hotelList.isEmpty()) {
            selectedHotel = hotelList.get(0);
        } else {
            selectedHotel = hotelRepository.findAll().stream().findFirst().orElse(null);
        }

        double pricePerNight = (selectedHotel != null && selectedHotel.getPricePerNight() != null)
                ? selectedHotel.getPricePerNight()
                : 2200.0;
        String hotelName = (selectedHotel != null && selectedHotel.getHotelName() != null)
                ? selectedHotel.getHotelName()
                : "Hotel City Center";

        // Assume number_of_nights = days - 1 (3 days = 2 nights)
        int numberOfNights = Math.max(0, days - 1);
        double accommodationCost = round2(pricePerNight * numberOfNights);

        BudgetEstimate.AccommodationDetail accommodationDetail = new BudgetEstimate.AccommodationDetail(
                hotelName,
                round2(pricePerNight),
                numberOfNights,
                accommodationCost
        );

        // 3. Food Calculation
        List<FoodPlace> foodList = foodPlaceRepository.findByPlaceId(placeId);
        FoodPlace selectedFood = null;
        if (!foodList.isEmpty()) {
            selectedFood = foodList.get(0);
        } else {
            selectedFood = foodPlaceRepository.findAll().stream().findFirst().orElse(null);
        }

        double pricePerMeal = (selectedFood != null && selectedFood.getPricePerMeal() != null)
                ? selectedFood.getPricePerMeal()
                : 150.0;
        String restaurantName = (selectedFood != null && selectedFood.getRestaurantName() != null)
                ? selectedFood.getRestaurantName()
                : "Kamath Govinda Restaurant";

        int mealsPerDay = 3;
        double foodCost = round2(pricePerMeal * mealsPerDay * travelers * days);

        BudgetEstimate.FoodDetail foodDetail = new BudgetEstimate.FoodDetail(
                restaurantName,
                round2(pricePerMeal),
                mealsPerDay,
                foodCost
        );

        // 4. Total Budget & Per Person
        double totalBudget = round2(transportationCost + accommodationCost + foodCost);
        double perPerson = round2(totalBudget / travelers);

        return new BudgetEstimate(
                placeId,
                place.getPlaceName(),
                days,
                travelers,
                transportationCost,
                accommodationCost,
                foodCost,
                totalBudget,
                perPerson,
                transportDetail,
                accommodationDetail,
                foodDetail
        );
    }

    private double estimateDistanceKm(TouristPlace place) {
        if (place.getLatitude() != null && place.getLongitude() != null) {
            double lat1 = 17.665;
            double lon1 = 75.908;
            double lat2 = place.getLatitude();
            double lon2 = place.getLongitude();

            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double directKm = 6371 * c;

            // Road routing estimate factor + local excursion
            double estimated = Math.max(6.0, Math.min(45.0, directKm * 1.35 + 4.0));
            return round2(estimated);
        }
        return 10.0;
    }

    private double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}