package com.smarttourism.smart_tourism_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "transportation")
public class Transportation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transport_id")
    private Integer transportId;

    @Column(name = "place_id", nullable = false)
    private Integer placeId;

    @Column(name = "transport_type", nullable = false)
    private String transportType;

    @Column(name = "base_fare", nullable = false)
    private Double baseFare;

    @Column(name = "fare_per_km", nullable = false)
    private Double farePerKm;

    public Transportation() {
    }

    public Transportation(Integer placeId, String transportType, Double baseFare, Double farePerKm) {
        this.placeId = placeId;
        this.transportType = transportType;
        this.baseFare = baseFare;
        this.farePerKm = farePerKm;
    }

    public Integer getTransportId() {
        return transportId;
    }

    public void setTransportId(Integer transportId) {
        this.transportId = transportId;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer placeId) {
        this.placeId = placeId;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
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
}

