package com.smarttourism.smart_tourism_backend.repository;

import com.smarttourism.smart_tourism_backend.entity.FoodPlace;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodPlaceRepository extends JpaRepository<FoodPlace, Integer> {

    List<FoodPlace> findByPlaceId(Integer placeId);

    Optional<FoodPlace> findFirstByPlaceId(Integer placeId);
}

