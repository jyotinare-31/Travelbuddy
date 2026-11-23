package com.smarttourism.smart_tourism_backend.dto;

public record RecommendedPlaceResponse(
        Integer placeId,
        String placeName,
        String category,
        String subCategory,
        String description,
        Double rating,
        Integer reviewCount,
        Double ticketPrice,
        String location,
        String imageUrl,
        Boolean isHidden,
        String reason,
        String speciality
) {
    public RecommendedPlaceResponse(
            Integer placeId,
            String placeName,
            String category,
            String subCategory,
            String description,
            Double rating,
            Integer reviewCount,
            Double ticketPrice,
            String location,
            String imageUrl,
            Boolean isHidden,
            String reason
    ) {
        this(placeId, placeName, category, subCategory, description, rating, reviewCount, ticketPrice, location, imageUrl, isHidden, reason, null);
    }
}
