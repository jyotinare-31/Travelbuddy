package com.smarttourism.smart_tourism_backend.controller;

import com.smarttourism.smart_tourism_backend.dto.RecommendationRequest;
import com.smarttourism.smart_tourism_backend.dto.RecommendedPlaceResponse;
import com.smarttourism.smart_tourism_backend.dto.SolapurGuideResponse;
import com.smarttourism.smart_tourism_backend.service.RecommendationService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public List<RecommendedPlaceResponse> getRecommendations(@RequestBody RecommendationRequest request) {
        return recommendationService.recommend(request);
    }

    @GetMapping("/solapur-guide")
    public SolapurGuideResponse getSolapurGuide() {
        return recommendationService.getSolapurGuide();
    }
}