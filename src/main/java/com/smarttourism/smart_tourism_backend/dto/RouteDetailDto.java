package com.smarttourism.smart_tourism_backend.dto;

import java.util.List;

public class RouteDetailDto {

    private String name;
    private double distanceKm;
    private double durationMinutes;
    private String formattedDuration;
    private List<List<Double>> geometry; // [[lon, lat], ...]
    private String summary;

    public RouteDetailDto() {}

    public RouteDetailDto(String name, double distanceKm, double durationMinutes,
                          String formattedDuration, List<List<Double>> geometry, String summary) {
        this.name = name;
        this.distanceKm = distanceKm;
        this.durationMinutes = durationMinutes;
        this.formattedDuration = formattedDuration;
        this.geometry = geometry;
        this.summary = summary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(double durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getFormattedDuration() {
        return formattedDuration;
    }

    public void setFormattedDuration(String formattedDuration) {
        this.formattedDuration = formattedDuration;
    }

    public List<List<Double>> getGeometry() {
        return geometry;
    }

    public void setGeometry(List<List<Double>> geometry) {
        this.geometry = geometry;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}

