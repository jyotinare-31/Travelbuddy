package com.smarttourism.smart_tourism_backend.dto;

public class RouteCompareRequest {

    private String startName;
    private Double startLat;
    private Double startLon;
    private String endName;
    private Double endLat;
    private Double endLon;
    private String transportMode; // "car", "walk", "bike"
    private String preference;    // "distance" or "time"

    public RouteCompareRequest() {}

    public RouteCompareRequest(String startName, Double startLat, Double startLon,
                               String endName, Double endLat, Double endLon,
                               String transportMode, String preference) {
        this.startName = startName;
        this.startLat = startLat;
        this.startLon = startLon;
        this.endName = endName;
        this.endLat = endLat;
        this.endLon = endLon;
        this.transportMode = transportMode;
        this.preference = preference;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLon() {
        return startLon;
    }

    public void setStartLon(Double startLon) {
        this.startLon = startLon;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public Double getEndLat() {
        return endLat;
    }

    public void setEndLat(Double endLat) {
        this.endLat = endLat;
    }

    public Double getEndLon() {
        return endLon;
    }

    public void setEndLon(Double endLon) {
        this.endLon = endLon;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }
}

