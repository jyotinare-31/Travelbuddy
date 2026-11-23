package com.smarttourism.smart_tourism_backend.controller;

import com.smarttourism.smart_tourism_backend.dto.RouteCompareRequest;
import com.smarttourism.smart_tourism_backend.dto.RouteCompareResponse;
import com.smarttourism.smart_tourism_backend.dto.RouteOptimizationRequest;
import com.smarttourism.smart_tourism_backend.dto.RouteOptimizationResponse;
import com.smarttourism.smart_tourism_backend.service.NavigationService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class NavigationController {

    private final NavigationService navigationService;

    public NavigationController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @GetMapping("/api/navigation")
    public String getRoute(
            @RequestParam double startLon,
            @RequestParam double startLat,
            @RequestParam double endLon,
            @RequestParam double endLat) {

        return navigationService.getRoute(
                startLon, startLat, endLon, endLat);
    }

    @PostMapping("/api/navigation/optimize-route")
    public RouteOptimizationResponse optimizeRoute(@RequestBody RouteOptimizationRequest request) {
        return navigationService.optimizeRoute(request);
    }

    @PostMapping("/api/navigation/compare-routes")
    public RouteCompareResponse compareRoutes(@RequestBody RouteCompareRequest request) {
        return navigationService.compareRoutes(request);
    }
}
