package com.smarttourism.smart_tourism_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NearbyService {

    @Value("${foursquare.api.key:${FOURSQUARE_API_KEY:}}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // ----------------------------------------------------
    // LIVE NEARBY PLACES
    // ----------------------------------------------------
    public String getNearbyPlaces(double latitude, double longitude) {

        String url = "https://places-api.foursquare.com/places/search"
                + "?ll=" + latitude + "," + longitude
                + "&radius=5000"
                + "&limit=20";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("X-Places-Api-Version", "2025-06-17");
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }


    // ----------------------------------------------------
    // LIVE NEARBY HOTELS
    // ----------------------------------------------------
    public String getNearbyHotels(double latitude, double longitude) {

        String url = "https://places-api.foursquare.com/places/search"
                + "?ll=" + latitude + "," + longitude
                + "&radius=5000"
                + "&limit=20"
                + "&query=hotel";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("X-Places-Api-Version", "2025-06-17");
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }
}