package com.smarttourism.smart_tourism_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ApiTestController {

    @Value("${openweather.api.key}")
    private String openWeatherKey;

    @Value("${openrouteservice.api.key}")
    private String openRouteKey;

    @Value("${aviationstack.api.key}")
    private String aviationStackKey;

    @Value("${foursquare.api.key}")
    private String foursquareKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/api/test-all")
    public String testAllApis() {

        StringBuilder result = new StringBuilder();

        // ==================================================
        // 1. OPENWEATHER
        // ==================================================
        try {

            String url =
                    "https://api.openweathermap.org/data/2.5/weather"
                    + "?q=Solapur"
                    + "&appid=" + openWeatherKey.trim()
                    + "&units=metric";

            String response =
                    restTemplate.getForObject(url, String.class);

            result.append("========== OPENWEATHER ==========\n");
            result.append("STATUS: WORKING\n");
            result.append(response);
            result.append("\n\n");

        } catch (Exception e) {

            result.append("========== OPENWEATHER ==========\n");
            result.append("STATUS: ERROR\n");
            result.append(e.getMessage());
            result.append("\n\n");
        }


        // ==================================================
        // 2. OPENROUTESERVICE
        // ==================================================
        try {

            String url =
                    "https://api.openrouteservice.org/v2/directions/driving-car"
                    + "?api_key=" + openRouteKey.trim()
                    + "&start=75.9167,17.6833"
                    + "&end=74.3587,16.8302";

            String response =
                    restTemplate.getForObject(url, String.class);

            result.append("========== OPENROUTESERVICE ==========\n");
            result.append("STATUS: WORKING\n");
            result.append(response);
            result.append("\n\n");

        } catch (Exception e) {

            result.append("========== OPENROUTESERVICE ==========\n");
            result.append("STATUS: ERROR\n");
            result.append(e.getMessage());
            result.append("\n\n");
        }


        // ==================================================
        // 3. AVIATIONSTACK
        // ==================================================
        try {

            String url =
                    "https://api.aviationstack.com/v1/flights"
                    + "?access_key=" + aviationStackKey.trim()
                    + "&limit=5";

            String response =
                    restTemplate.getForObject(url, String.class);

            result.append("========== AVIATIONSTACK ==========\n");
            result.append("STATUS: WORKING\n");
            result.append(response);
            result.append("\n\n");

        } catch (Exception e) {

            result.append("========== AVIATIONSTACK ==========\n");
            result.append("STATUS: ERROR\n");
            result.append(e.getMessage());
            result.append("\n\n");
        }


        // ==================================================
        // 4. FOURSQUARE
        // ==================================================
        try {

            String url =
                    "https://places-api.foursquare.com/places/search"
                    + "?ll=17.6833,75.9167"
                    + "&limit=5";

            HttpHeaders headers = new HttpHeaders();

            headers.set(
                    "Authorization",
                    "Bearer " + foursquareKey.trim()
            );

            headers.set(
                    "Accept",
                    "application/json"
            );

            headers.set(
                    "X-Places-Api-Version",
                    "2025-06-17"
            );

            HttpEntity<String> entity =
                    new HttpEntity<>(headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity,
                            String.class
                    );

            result.append("========== FOURSQUARE ==========\n");
            result.append("STATUS: WORKING\n");
            result.append(response.getBody());
            result.append("\n\n");

        } catch (Exception e) {

            result.append("========== FOURSQUARE ==========\n");
            result.append("STATUS: ERROR\n");
            result.append(e.getMessage());
            result.append("\n\n");
        }


        // Return all API results
        return result.toString();
    }
}