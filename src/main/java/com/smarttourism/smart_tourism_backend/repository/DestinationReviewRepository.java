package com.smarttourism.smart_tourism_backend.repository;

import com.smarttourism.smart_tourism_backend.entity.DestinationReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationReviewRepository extends JpaRepository<DestinationReview, Integer> {
    List<DestinationReview> findByDestinationIdOrderByCreatedAtDesc(Integer destinationId);
    List<DestinationReview> findByUserUserIdOrderByCreatedAtDesc(Integer userId);
    int countByUserUserId(Integer userId);

    @Query("SELECT AVG(r.rating) FROM DestinationReview r WHERE r.destinationId = :destinationId")
    Double getAverageRatingForDestination(@Param("destinationId") Integer destinationId);
}

