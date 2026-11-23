package com.smarttourism.smart_tourism_backend.dto;

import java.util.List;

public record SolapurGuideResponse(
        List<FamousPlaceItem> famousPlaces,
        List<FamousFoodItem> famousFoods,
        List<DistrictSpecialityItem> districtSpecialities
) {
    public record FamousPlaceItem(
            String name,
            String category,
            String location,
            String speciality,
            String description,
            String imageUrl,
            String timing,
            String entryFee
    ) {}

    public record FamousFoodItem(
            String name,
            String localName,
            String category,
            String description,
            String highlight,
            String pairing,
            String whereToTry,
            String imageUrl
    ) {}

    public record DistrictSpecialityItem(
            String title,
            String category,
            String description,
            String culturalSignificance,
            String shoppingTip,
            String icon
    ) {}
}

