package com.smarttourism.smart_tourism_backend.controller;

import com.smarttourism.smart_tourism_backend.entity.TouristPlace;
import com.smarttourism.smart_tourism_backend.repository.TouristPlaceRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/tourist-places", "/api/places"})
@CrossOrigin(origins = "*")
public class TouristPlaceController {

    private final TouristPlaceRepository repository;

    public TouristPlaceController(TouristPlaceRepository repository) {
        this.repository = repository;
    }

    // Get all tourist places
    @GetMapping
    public List<TouristPlace> getAllPlaces() {
        return repository.findAll();
    }

    // Get all hidden destinations
    @GetMapping("/hidden")
    public List<TouristPlace> getHiddenDestinations() {
        return repository.findByIsHiddenTrue();
    }

    // Get one tourist place by ID
    @GetMapping("/{id}")
    public TouristPlace getPlaceById(@PathVariable Integer id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tourist place not found with ID: " + id
                        )
                );
    }
}