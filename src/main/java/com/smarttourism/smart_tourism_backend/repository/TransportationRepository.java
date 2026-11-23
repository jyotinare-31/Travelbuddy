package com.smarttourism.smart_tourism_backend.repository;

import com.smarttourism.smart_tourism_backend.entity.Transportation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportationRepository extends JpaRepository<Transportation, Integer> {

    List<Transportation> findByPlaceId(Integer placeId);

    Optional<Transportation> findFirstByPlaceIdAndTransportTypeIgnoreCase(Integer placeId, String transportType);

    Optional<Transportation> findFirstByPlaceId(Integer placeId);
}

