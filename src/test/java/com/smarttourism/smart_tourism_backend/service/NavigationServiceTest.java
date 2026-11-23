package com.smarttourism.smart_tourism_backend.service;

import com.smarttourism.smart_tourism_backend.dto.RouteOptimizationRequest;
import com.smarttourism.smart_tourism_backend.dto.RouteOptimizationResponse;
import com.smarttourism.smart_tourism_backend.entity.TouristPlace;
import com.smarttourism.smart_tourism_backend.repository.TouristPlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NavigationServiceTest {

    private TouristPlaceRepository touristPlaceRepository;
    private NavigationService navigationService;

    @BeforeEach
    void setUp() {
        touristPlaceRepository = mock(TouristPlaceRepository.class);
        navigationService = new NavigationService(
                "test-api-key",
                touristPlaceRepository,
                new ObjectMapper()
        );
    }

    private TouristPlace createPlace(int id, String name, double lat, double lon) {
        TouristPlace p = new TouristPlace();
        p.setPlaceId(id);
        p.setPlaceName(name);
        p.setLocation("Solapur");
        p.setCategory("Historical");
        p.setLatitude(lat);
        p.setLongitude(lon);
        return p;
    }

    @Test
    void rejectsFewerThanTwoDestinations() {
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setDestinationIds(List.of(1));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> navigationService.optimizeRoute(request));

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("At least two"));
    }

    @Test
    void rejectsDestinationWithMissingCoordinates() {
        TouristPlace p1 = createPlace(1, "Place A", 17.65, 75.90);
        TouristPlace p2 = new TouristPlace();
        p2.setPlaceId(2);
        p2.setPlaceName("Place B without coords");

        when(touristPlaceRepository.findAllById(List.of(1, 2)))
                .thenReturn(List.of(p1, p2));

        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setDestinationIds(List.of(1, 2));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> navigationService.optimizeRoute(request));

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("missing or invalid coordinates"));
    }

    @Test
    void preservesFixedStartAndEndWhileOrderingIntermediates() {
        // Place 1: Siddheshwar (17.6599, 75.9064)
        TouristPlace p1 = createPlace(1, "Siddheshwar Temple", 17.6599, 75.9064);
        // Place 2: Naldurg (17.8183, 76.2974) - further east
        TouristPlace p2 = createPlace(2, "Naldurg Fort", 17.8183, 76.2974);
        // Place 3: Bhuikot (17.6685, 75.9038) - right near Siddheshwar in Solapur city
        TouristPlace p3 = createPlace(3, "Bhuikot Fort", 17.6685, 75.9038);

        when(touristPlaceRepository.findAllById(List.of(1, 2, 3)))
                .thenReturn(List.of(p1, p2, p3));

        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setDestinationIds(List.of(1, 2, 3));
        request.setStartDestinationId(1); // Start at Siddheshwar
        request.setFinalDestinationId(2); // End at Naldurg

        RouteOptimizationResponse response = navigationService.optimizeRoute(request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(3, response.getOrderedStops().size());

        // First must be Siddheshwar
        assertEquals(1, response.getOrderedStops().get(0).getPlaceId());
        assertTrue(response.getOrderedStops().get(0).isStart());

        // Intermediate must be Bhuikot Fort (close to start before travelling to Naldurg)
        assertEquals(3, response.getOrderedStops().get(1).getPlaceId());

        // Final must be Naldurg Fort
        assertEquals(2, response.getOrderedStops().get(2).getPlaceId());
        assertTrue(response.getOrderedStops().get(2).isFinal());
    }

    @Test
    void calculatesHaversineDistanceAccurately() {
        TouristPlace p1 = createPlace(1, "Solapur City", 17.6599, 75.9064);
        TouristPlace p2 = createPlace(2, "Pandharpur", 17.6778, 75.3267);

        double dist = NavigationService.haversineDistance(p1, p2);
        // Distance is ~61-62 km
        assertTrue(dist > 60.0 && dist < 65.0, "Haversine distance should be around 61 km");
    }
}

