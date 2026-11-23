package com.smarttourism.smart_tourism_backend.dto;

import java.util.List;

public class RouteOptimizationRequest {

    private List<Integer> destinationIds;
    private Integer startDestinationId;
    private Integer finalDestinationId;
    private String transportMode;
    private String optimizationCriteria;

    public RouteOptimizationRequest() {
    }

    public RouteOptimizationRequest(
            List<Integer> destinationIds,
            Integer startDestinationId,
            Integer finalDestinationId,
            String transportMode,
            String optimizationCriteria) {
        this.destinationIds = destinationIds;
        this.startDestinationId = startDestinationId;
        this.finalDestinationId = finalDestinationId;
        this.transportMode = transportMode;
        this.optimizationCriteria = optimizationCriteria;
    }

    public List<Integer> getDestinationIds() {
        return destinationIds;
    }

    public void setDestinationIds(List<Integer> destinationIds) {
        this.destinationIds = destinationIds;
    }

    public Integer getStartDestinationId() {
        return startDestinationId;
    }

    public void setStartDestinationId(Integer startDestinationId) {
        this.startDestinationId = startDestinationId;
    }

    public Integer getFinalDestinationId() {
        return finalDestinationId;
    }

    public void setFinalDestinationId(Integer finalDestinationId) {
        this.finalDestinationId = finalDestinationId;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public String getOptimizationCriteria() {
        return optimizationCriteria;
    }

    public void setOptimizationCriteria(String optimizationCriteria) {
        this.optimizationCriteria = optimizationCriteria;
    }
}

