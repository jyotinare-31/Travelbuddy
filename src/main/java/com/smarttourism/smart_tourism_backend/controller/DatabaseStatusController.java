package com.smarttourism.smart_tourism_backend.controller;

import com.smarttourism.smart_tourism_backend.repository.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/db-status")
public class DatabaseStatusController {

    private final DataSource dataSource;
    private final UserRepository userRepository;
    private final TouristPlaceRepository touristPlaceRepository;
    private final HotelRepository hotelRepository;
    private final CommunityPostRepository communityPostRepository;
    private final DestinationReviewRepository destinationReviewRepository;

    public DatabaseStatusController(
            DataSource dataSource,
            UserRepository userRepository,
            TouristPlaceRepository touristPlaceRepository,
            HotelRepository hotelRepository,
            CommunityPostRepository communityPostRepository,
            DestinationReviewRepository destinationReviewRepository) {
        this.dataSource = dataSource;
        this.userRepository = userRepository;
        this.touristPlaceRepository = touristPlaceRepository;
        this.hotelRepository = hotelRepository;
        this.communityPostRepository = communityPostRepository;
        this.destinationReviewRepository = destinationReviewRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getDatabaseStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("timestamp", LocalDateTime.now().toString());

        try (Connection connection = dataSource.getConnection()) {
            boolean valid = connection.isValid(3);
            DatabaseMetaData metaData = connection.getMetaData();

            status.put("connected", valid);
            status.put("status", "UP");
            status.put("databaseProduct", metaData.getDatabaseProductName());
            status.put("databaseVersion", metaData.getDatabaseProductVersion());
            status.put("driverName", metaData.getDriverName());
            status.put("driverVersion", metaData.getDriverVersion());
            status.put("url", metaData.getURL());
            status.put("username", metaData.getUserName());

            // Read table counts to verify JPA & PostgreSQL data integrity
            Map<String, Object> tables = new LinkedHashMap<>();
            tables.put("tourist_places", touristPlaceRepository.count());
            tables.put("users", userRepository.count());
            tables.put("hotels", hotelRepository.count());
            tables.put("community_posts", communityPostRepository.count());
            tables.put("destination_reviews", destinationReviewRepository.count());

            status.put("tables", tables);
            status.put("message", "PostgreSQL database connected and verified successfully.");

            return ResponseEntity.ok(status);
        } catch (Exception ex) {
            status.put("connected", false);
            status.put("status", "DOWN");
            status.put("error", ex.getClass().getSimpleName());
            status.put("message", "Database connection error: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(status);
        }
    }
}

