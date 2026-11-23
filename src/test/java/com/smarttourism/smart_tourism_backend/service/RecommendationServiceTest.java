package com.smarttourism.smart_tourism_backend.service;

import com.smarttourism.smart_tourism_backend.dto.RecommendationRequest;
import com.smarttourism.smart_tourism_backend.dto.RecommendedPlaceResponse;
import com.smarttourism.smart_tourism_backend.entity.TouristPlace;
import com.smarttourism.smart_tourism_backend.repository.TouristPlaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RecommendationServiceTest {

    @Test
    void filtersActualDatabasePlacesAndReturnsTheirStoredFacts() {
        TouristPlace matching = place(1, "Bhuikot Fort", "Historical", 4.5, 100.0);
        TouristPlace lowRated = place(2, "Low-rated Fort", "Historical", 2.0, 100.0);
        TouristPlace overBudget = place(3, "Expensive Fort", "Historical", 4.9, 600.0);
        TouristPlace wrongCategory = place(4, "Lake", "Nature", 4.9, 0.0);

        TouristPlaceRepository repository = mock(TouristPlaceRepository.class);
        when(repository.findAll()).thenReturn(List.of(matching, lowRated, overBudget, wrongCategory));
        GeminiService geminiService = mock(GeminiService.class);
        when(geminiService.recommendPlaces(
                anyList(),
                eq("Solapur"),
                eq(500.0),
                eq(2),
                eq("Walking"),
                eq(List.of("History")),
                eq("Historical"),
                eq(4.0)
        )).thenReturn(List.of(new GeminiService.PlaceRecommendation(1, "Matches your history interest.")));
        RecommendationService service = new RecommendationService(repository, geminiService);

        List<RecommendedPlaceResponse> results = service.recommend(request());

        assertEquals(1, results.size());
        assertEquals(1, results.getFirst().placeId());
        assertEquals("Bhuikot Fort", results.getFirst().placeName());
        assertEquals(4.5, results.getFirst().rating());
        assertEquals(100.0, results.getFirst().ticketPrice());
        assertEquals("Matches your history interest.", results.getFirst().reason());
        verify(geminiService).recommendPlaces(
                List.of(matching),
                "Solapur",
                500.0,
                2,
                "Walking",
                List.of("History"),
                "Historical",
                4.0
        );
    }

    @Test
    void doesNotCallGeminiWhenDatabaseFiltersFindNoMatchingPlaces() {
        TouristPlaceRepository repository = mock(TouristPlaceRepository.class);
        when(repository.findAll()).thenReturn(List.of(place(1, "Lake", "Nature", 4.8, 0.0)));
        GeminiService geminiService = mock(GeminiService.class);
        RecommendationService service = new RecommendationService(repository, geminiService);
        RecommendationRequest request = request();
        request.setPreferredCategory("Historical");

        assertEquals(List.of(), service.recommend(request));
        verifyNoInteractions(geminiService);
    }

    @Test
    void rejectsInvalidTripDuration() {
        TouristPlaceRepository repository = mock(TouristPlaceRepository.class);
        RecommendationService service = new RecommendationService(repository, mock(GeminiService.class));
        RecommendationRequest request = request();
        request.setDays(31);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.recommend(request)
        );

        assertEquals(400, exception.getStatusCode().value());
        verify(repository, never()).findAll();
    }

    private RecommendationRequest request() {
        RecommendationRequest request = new RecommendationRequest();
        request.setLocation("Solapur");
        request.setBudget(500);
        request.setDays(2);
        request.setTravelPreference("Walking");
        request.setInterests(List.of("History"));
        request.setPreferredCategory("Historical");
        request.setMinimumRating(4.0);
        return request;
    }

    private TouristPlace place(Integer id, String name, String category, Double rating, Double ticketPrice) {
        TouristPlace place = new TouristPlace();
        place.setPlaceId(id);
        place.setPlaceName(name);
        place.setCategory(category);
        place.setSubCategory(category);
        place.setDescription("Database description for " + name);
        place.setRating(rating);
        place.setReviewCount(12);
        place.setTicketPrice(ticketPrice);
        place.setLocation("Solapur");
        place.setImageUrl("/images/" + id + ".webp");
        place.setIsHidden(id == 1);
        return place;
    }
}
