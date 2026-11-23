package com.smarttourism.smart_tourism_backend.dto;

public class RouteCompareResponse {

    private boolean success;
    private String message;
    private boolean hasAlternative;
    private String startName;
    private double startLat;
    private double startLon;
    private String endName;
    private double endLat;
    private double endLon;
    private String transportMode;
    private String optimizationObjective;
    private RouteDetailDto originalRoute;
    private RouteDetailDto optimizedRoute;
    private double distanceSavedKm;
    private double timeSavedMinutes;
    private String formattedTimeSaved;
    private double percentReduction;
    private String externalNavigationUrl;

    public RouteCompareResponse() {}

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

    public boolean isHasAlternative() {
        return hasAlternative;
    }

    public void setHasAlternative(boolean hasAlternative) {
        this.hasAlternative = hasAlternative;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLon() {
        return startLon;
    }

    public void setStartLon(double startLon) {
        this.startLon = startLon;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLon() {
        return endLon;
    }

    public void setEndLon(double endLon) {
        this.endLon = endLon;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public String getOptimizationObjective() {
        return optimizationObjective;
    }

    public void setOptimizationObjective(String optimizationObjective) {
        this.optimizationObjective = optimizationObjective;
    }

    public RouteDetailDto getOriginalRoute() {
        return originalRoute;
    }

    public void setOriginalRoute(RouteDetailDto originalRoute) {
        this.originalRoute = originalRoute;
    }

    public RouteDetailDto getOptimizedRoute() {
        return optimizedRoute;
    }

    public void setOptimizedRoute(RouteDetailDto optimizedRoute) {
        this.optimizedRoute = optimizedRoute;
    }

    public double getDistanceSavedKm() {
        return distanceSavedKm;
    }

    public void setDistanceSavedKm(double distanceSavedKm) {
        this.distanceSavedKm = distanceSavedKm;
    }

    public double getTimeSavedMinutes() {
        return timeSavedMinutes;
    }

    public void setTimeSavedMinutes(double timeSavedMinutes) {
        this.timeSavedMinutes = timeSavedMinutes;
    }

    public String getFormattedTimeSaved() {
        return formattedTimeSaved;
    }

    public void setFormattedTimeSaved(String formattedTimeSaved) {
        this.formattedTimeSaved = formattedTimeSaved;
    }

    public double getPercentReduction() {
        return percentReduction;
    }

    public void setPercentReduction(double percentReduction) {
        this.percentReduction = percentReduction;
    }

    public String getExternalNavigationUrl() {
        return externalNavigationUrl;
    }

    public void setExternalNavigationUrl(String externalNavigationUrl) {
        this.externalNavigationUrl = externalNavigationUrl;
    }
}

