package com.smarttourism.smart_tourism_backend.repository;

import com.smarttourism.smart_tourism_backend.entity.TouristPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TouristPlaceRepository extends JpaRepository<TouristPlace, Integer> {

    // Fetch all places marked as Hidden Destinations
    List<TouristPlace> findByIsHiddenTrue();
}