package com.smarttourism.smart_tourism_backend.controller;

import com.smarttourism.smart_tourism_backend.service.FlightService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/api/flights")
    public String getFlights(
            @RequestParam String depIata,
            @RequestParam String arrIata) {

        return flightService.getFlights(depIata, arrIata);
    }
}