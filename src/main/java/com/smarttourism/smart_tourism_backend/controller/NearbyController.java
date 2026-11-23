package com.smarttourism.smart_tourism_backend.controller;

import com.smarttourism.smart_tourism_backend.service.NearbyService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/live")
@CrossOrigin(origins = "*")
public class NearbyController {

    private final NearbyService nearbyService;

    public NearbyController(NearbyService nearbyService) {
        this.nearbyService = nearbyService;
    }

    // ----------------------------------------------------
    // LIVE NEARBY PLACES
    // ----------------------------------------------------
    @GetMapping("/nearby")
    public String getNearbyPlaces(
            @RequestParam double latitude,
            @RequestParam double longitude) {

        return nearbyService.getNearbyPlaces(
                latitude,
                longitude
        );
    }

    // ----------------------------------------------------
    // LIVE NEARBY HOTELS
    // ----------------------------------------------------
    @GetMapping("/hotels")
    public String getNearbyHotels(
            @RequestParam double latitude,
            @RequestParam double longitude) {

        return nearbyService.getNearbyHotels(
                latitude,
                longitude
        );
    }
}