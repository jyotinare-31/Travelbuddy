package com.smarttourism.smart_tourism_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Service
public class HotelbedsService {

    private final String apiKey;
    private final String secret;
    private final String baseUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public HotelbedsService(
            @Value("${hotelbeds.api.key:}") String apiKey,
            @Value("${hotelbeds.secret:}") String secret,
            @Value("${hotelbeds.api.base-url:https://api.hotelbeds.com}") String baseUrl) {
        this(apiKey, secret, baseUrl, new RestTemplate());
    }

    HotelbedsService(String apiKey, String secret, String baseUrl, RestTemplate restTemplate) {
        this.apiKey = apiKey;
        this.secret = secret;
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    public String getLiveHotelRates(
            String destinationCode,
            LocalDate checkIn,
            LocalDate checkOut,
            int rooms,
            int adults) {
        if (destinationCode == null || destinationCode.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "destinationCode is required");
        }
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new ResponseStatusException(BAD_REQUEST, "checkOut must be after checkIn");
        }
        if (rooms < 1 || adults < 1) {
            throw new ResponseStatusException(BAD_REQUEST, "rooms and adults must be at least 1");
        }
        if (apiKey.isBlank() || secret.isBlank()) {
            throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Hotelbeds API credentials are not configured");
        }

        Map<String, Object> requestBody = Map.of(
                "stay", Map.of("checkIn", checkIn.toString(), "checkOut", checkOut.toString()),
                "occupancies", List.of(Map.of("rooms", rooms, "adults", adults, "children", 0)),
                "destination", Map.of("code", destinationCode.trim())
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Api-Key", apiKey);
        headers.set("X-Signature", createSignature(apiKey, secret));
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = baseUrl.replaceAll("/+$", "") + "/hotel-api/1.0/hotels";
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                String.class
        );
        return response.getBody();
    }

    private String createSignature(String key, String apiSecret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    (key + apiSecret + Instant.now().getEpochSecond()).getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }
}
