package com.smarttourism.smart_tourism_backend.dto;

import java.util.List;

public class RouteOptimizationResponse {

    private boolean success;
    private String message;
    private List<RouteStopDto> orderedStops;
    private double totalDistanceKm;
    private double totalDurationMinutes;
    private String formattedTotalDuration;
    private String startDestinationName;
    private String finalDestinationName;
    private String transportMode;
    private List<List<Double>> geometryCoordinates;
    private String externalNavigationUrl;
    private int ecoScore;
    private String ecoMessage;

    public RouteOptimizationResponse() {
    }

    public RouteOptimizationResponse(
            boolean success,
            String message,
            List<RouteStopDto> orderedStops,
            double totalDistanceKm,
            double totalDurationMinutes,
            String formattedTotalDuration,
            String startDestinationName,
            String finalDestinationName,
            String transportMode,
            List<List<Double>> geometryCoordinates,
            String externalNavigationUrl,
            int ecoScore,
            String ecoMessage) {
        this.success = success;
        this.message = message;
        this.orderedStops = orderedStops;
        this.totalDistanceKm = totalDistanceKm;
        this.totalDurationMinutes = totalDurationMinutes;
        this.formattedTotalDuration = formattedTotalDuration;
        this.startDestinationName = startDestinationName;
        this.finalDestinationName = finalDestinationName;
        this.transportMode = transportMode;
        this.geometryCoordinates = geometryCoordinates;
        this.externalNavigationUrl = externalNavigationUrl;
        this.ecoScore = ecoScore;
        this.ecoMessage = ecoMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RouteStopDto> getOrderedStops() {
        return orderedStops;
    }

    public void setOrderedStops(List<RouteStopDto> orderedStops) {
        this.orderedStops = orderedStops;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public double getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public void setTotalDurationMinutes(double totalDurationMinutes) {
        this.totalDurationMinutes = totalDurationMinutes;
    }

    public String getFormattedTotalDuration() {
        return formattedTotalDuration;
    }

    public void setFormattedTotalDuration(String formattedTotalDuration) {
        this.formattedTotalDuration = formattedTotalDuration;
    }

    public String getStartDestinationName() {
        return startDestinationName;
    }

    public void setStartDestinationName(String startDestinationName) {
        this.startDestinationName = startDestinationName;
    }

    public String getFinalDestinationName() {
        return finalDestinationName;
    }

    public void setFinalDestinationName(String finalDestinationName) {
        this.finalDestinationName = finalDestinationName;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public List<List<Double>> getGeometryCoordinates() {
        return geometryCoordinates;
    }

    public void setGeometryCoordinates(List<List<Double>> geometryCoordinates) {
        this.geometryCoordinates = geometryCoordinates;
    }

    public String getExternalNavigationUrl() {
        return externalNavigationUrl;
    }

    public void setExternalNavigationUrl(String externalNavigationUrl) {
        this.externalNavigationUrl = externalNavigationUrl;
    }

    public int getEcoScore() {
        return ecoScore;
    }

    public void setEcoScore(int ecoScore) {
        this.ecoScore = ecoScore;
    }

    public String getEcoMessage() {
        return ecoMessage;
    }

    public void setEcoMessage(String ecoMessage) {
        this.ecoMessage = ecoMessage;
    }
}

