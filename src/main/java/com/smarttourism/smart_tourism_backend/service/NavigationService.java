package com.smarttourism.smart_tourism_backend.service;

import com.smarttourism.smart_tourism_backend.dto.RouteCompareRequest;
import com.smarttourism.smart_tourism_backend.dto.RouteCompareResponse;
import com.smarttourism.smart_tourism_backend.dto.RouteDetailDto;
import com.smarttourism.smart_tourism_backend.dto.RouteOptimizationRequest;
import com.smarttourism.smart_tourism_backend.dto.RouteOptimizationResponse;
import com.smarttourism.smart_tourism_backend.dto.RouteStopDto;
import com.smarttourism.smart_tourism_backend.entity.TouristPlace;
import com.smarttourism.smart_tourism_backend.repository.TouristPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NavigationService {

    private static final Logger log = LoggerFactory.getLogger(NavigationService.class);
    private static final double EARTH_RADIUS_KM = 6371.0;

    private final String apiKey;
    private final TouristPlaceRepository touristPlaceRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public NavigationService(
            @Value("${openrouteservice.api.key:${OPENROUTESERVICE_API_KEY:}}") String apiKey,
            TouristPlaceRepository touristPlaceRepository,
            ObjectMapper objectMapper) {
        this(apiKey, touristPlaceRepository, new RestTemplate(), objectMapper);
    }

    NavigationService(
            String apiKey,
            TouristPlaceRepository touristPlaceRepository,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.touristPlaceRepository = touristPlaceRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
    }

    /**
     * Backward-compatible simple point-to-point route.
     */
    public String getRoute(
            double startLon,
            double startLat,
            double endLon,
            double endLat) {

        String url = "https://api.openrouteservice.org/v2/directions/driving-car"
                + "?api_key=" + apiKey
                + "&start=" + startLon + "," + startLat
                + "&end=" + endLon + "," + endLat;

        return restTemplate.getForObject(url, String.class);
    }

    /**
     * Solapur Multi-Destination Route Optimization:
     * 1. Validates selected destinations from PostgreSQL database.
     * 2. Preserves user-chosen start and final destinations.
     * 3. Calculates an efficient visiting order using waypoint permutation / 2-opt heuristic.
     * 4. Queries OpenRouteService for real road geometry, road distances, and travel durations.
     */
    public RouteOptimizationResponse optimizeRoute(RouteOptimizationRequest request) {
        if (request == null || request.getDestinationIds() == null || request.getDestinationIds().size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At least two valid tourist destinations must be selected to calculate a route.");
        }

        List<Integer> destinationIds = request.getDestinationIds().stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (destinationIds.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At least two distinct tourist destinations must be selected.");
        }

        List<TouristPlace> foundPlaces = touristPlaceRepository.findAllById(destinationIds);
        Map<Integer, TouristPlace> placeMap = foundPlaces.stream()
                .collect(Collectors.toMap(TouristPlace::getPlaceId, p -> p));

        // Validate all IDs were found
        for (Integer id : destinationIds) {
            if (!placeMap.containsKey(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tourist destination with ID " + id + " was not found in the database.");
            }
        }

        // Validate coordinates
        for (TouristPlace p : foundPlaces) {
            if (p.getLatitude() == null || p.getLongitude() == null
                    || !Double.isFinite(p.getLatitude()) || !Double.isFinite(p.getLongitude())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Destination '" + p.getPlaceName() + "' (ID: " + p.getPlaceId()
                                + ") has missing or invalid coordinates.");
            }
        }

        // 1. Determine Starting Destination
        TouristPlace startPlace = null;
        if (request.getStartDestinationId() != null && placeMap.containsKey(request.getStartDestinationId())) {
            startPlace = placeMap.get(request.getStartDestinationId());
        } else {
            startPlace = placeMap.get(destinationIds.get(0));
        }

        // 2. Determine Final Destination (optional fixed end)
        TouristPlace finalPlace = null;
        if (request.getFinalDestinationId() != null
                && !request.getFinalDestinationId().equals(startPlace.getPlaceId())
                && placeMap.containsKey(request.getFinalDestinationId())) {
            finalPlace = placeMap.get(request.getFinalDestinationId());
        }

        // 3. Pool of intermediate places to optimize
        List<TouristPlace> intermediatePlaces = new ArrayList<>();
        for (Integer id : destinationIds) {
            if (!id.equals(startPlace.getPlaceId())
                    && (finalPlace == null || !id.equals(finalPlace.getPlaceId()))) {
                intermediatePlaces.add(placeMap.get(id));
            }
        }

        // 4. Calculate efficient visiting order
        List<TouristPlace> orderedPlaces = orderWaypoints(startPlace, intermediatePlaces, finalPlace);

        // 5. Query OpenRouteService for exact road geometry, turn segments, distances, and duration
        String profile = resolveOrsProfile(request.getTransportMode());
        return queryOpenRouteService(orderedPlaces, profile, request.getTransportMode());
    }

    /**
     * Efficient TSP waypoint ordering using permutation for small sets or 2-opt heuristic.
     */
    private List<TouristPlace> orderWaypoints(
            TouristPlace start,
            List<TouristPlace> intermediates,
            TouristPlace fixedEnd) {

        List<TouristPlace> result = new ArrayList<>();
        result.add(start);

        if (intermediates.isEmpty()) {
            if (fixedEnd != null) {
                result.add(fixedEnd);
            }
            return result;
        }

        if (intermediates.size() <= 8) {
            // Exact optimal permutation search
            List<TouristPlace> bestPerm = findBestPermutation(start, intermediates, fixedEnd);
            result.addAll(bestPerm);
        } else {
            // Nearest Neighbor + 2-opt heuristic
            List<TouristPlace> heuristicOrder = nearestNeighborWith2Opt(start, intermediates, fixedEnd);
            result.addAll(heuristicOrder);
        }

        if (fixedEnd != null) {
            result.add(fixedEnd);
        }

        return result;
    }

    private List<TouristPlace> findBestPermutation(
            TouristPlace start,
            List<TouristPlace> items,
            TouristPlace fixedEnd) {

        List<List<TouristPlace>> permutations = new ArrayList<>();
        generatePermutations(items, 0, permutations);

        double minDistance = Double.MAX_VALUE;
        List<TouristPlace> best = items;

        for (List<TouristPlace> perm : permutations) {
            double totalDist = calculatePathDistance(start, perm, fixedEnd);
            if (totalDist < minDistance) {
                minDistance = totalDist;
                best = perm;
            }
        }

        return best;
    }

    private void generatePermutations(
            List<TouristPlace> list,
            int index,
            List<List<TouristPlace>> result) {
        if (index == list.size()) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = index; i < list.size(); i++) {
            Collections.swap(list, index, i);
            generatePermutations(list, index + 1, result);
            Collections.swap(list, index, i);
        }
    }

    private double calculatePathDistance(
            TouristPlace start,
            List<TouristPlace> intermediates,
            TouristPlace fixedEnd) {

        double dist = 0.0;
        TouristPlace current = start;
        for (TouristPlace next : intermediates) {
            dist += haversineDistance(current, next);
            current = next;
        }
        if (fixedEnd != null) {
            dist += haversineDistance(current, fixedEnd);
        }
        return dist;
    }

    private List<TouristPlace> nearestNeighborWith2Opt(
            TouristPlace start,
            List<TouristPlace> pool,
            TouristPlace fixedEnd) {

        List<TouristPlace> remaining = new ArrayList<>(pool);
        List<TouristPlace> tour = new ArrayList<>();
        TouristPlace current = start;

        while (!remaining.isEmpty()) {
            TouristPlace nearest = null;
            double minDist = Double.MAX_VALUE;
            for (TouristPlace candidate : remaining) {
                double d = haversineDistance(current, candidate);
                if (d < minDist) {
                    minDist = d;
                    nearest = candidate;
                }
            }
            tour.add(nearest);
            remaining.remove(nearest);
            current = nearest;
        }

        // Apply 2-opt improvements
        boolean improved = true;
        int maxPasses = 50;
        while (improved && maxPasses-- > 0) {
            improved = false;
            for (int i = 0; i < tour.size() - 1; i++) {
                for (int j = i + 1; j < tour.size(); j++) {
                    double currentDist = calculatePathDistance(start, tour, fixedEnd);
                    Collections.reverse(tour.subList(i, j + 1));
                    double newDist = calculatePathDistance(start, tour, fixedEnd);
                    if (newDist < currentDist) {
                        improved = true;
                    } else {
                        Collections.reverse(tour.subList(i, j + 1)); // revert
                    }
                }
            }
        }

        return tour;
    }

    public static double haversineDistance(TouristPlace a, TouristPlace b) {
        double lat1 = Math.toRadians(a.getLatitude());
        double lon1 = Math.toRadians(a.getLongitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double lon2 = Math.toRadians(b.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(h));
    }

    private String resolveOrsProfile(String mode) {
        if (mode == null) return "driving-car";
        String normalized = mode.trim().toLowerCase();
        return switch (normalized) {
            case "walk", "walking", "foot", "foot-walking" -> "foot-walking";
            case "bike", "bicycle", "cycling", "cycling-regular" -> "cycling-regular";
            default -> "driving-car";
        };
    }

    private RouteOptimizationResponse queryOpenRouteService(
            List<TouristPlace> orderedPlaces,
            String profile,
            String originalMode) {

        List<List<Double>> coordinates = orderedPlaces.stream()
                .map(p -> List.of(p.getLongitude(), p.getLatitude()))
                .toList();

        Map<String, Object> requestBody = Map.of("coordinates", coordinates);
        String orsUrl = "https://api.openrouteservice.org/v2/directions/" + profile + "/geojson";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!apiKey.isEmpty()) {
            headers.set("Authorization", apiKey);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            String jsonResponse = restTemplate.postForObject(orsUrl, entity, String.class);
            return parseOrsGeoJson(jsonResponse, orderedPlaces, profile, originalMode);
        } catch (HttpStatusCodeException e) {
            log.warn("OpenRouteService returned status {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            String errorMsg = "Routing API error (" + e.getStatusCode() + "). ";
            if (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
                errorMsg += "Please verify OPENROUTESERVICE_API_KEY.";
            } else if (e.getStatusCode().value() == 429) {
                errorMsg += "OpenRouteService daily rate limit exceeded.";
            } else {
                errorMsg += e.getStatusText();
            }
            // Fallback estimation using real stop sequence and Haversine distances
            return buildFallbackResponse(orderedPlaces, originalMode, errorMsg);
        } catch (Exception e) {
            log.error("Failed to connect to OpenRouteService: {}", e.getMessage());
            return buildFallbackResponse(orderedPlaces, originalMode,
                    "Routing service network failure: " + e.getMessage());
        }
    }

    private RouteOptimizationResponse parseOrsGeoJson(
            String jsonResponse,
            List<TouristPlace> orderedPlaces,
            String profile,
            String originalMode) {

        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode feature = root.path("features").path(0);
            JsonNode properties = feature.path("properties");
            JsonNode summary = properties.path("summary");
            JsonNode segments = properties.path("segments");
            JsonNode geometryCoords = feature.path("geometry").path("coordinates");

            double totalDistanceMeters = summary.path("distance").asDouble(0.0);
            double totalDurationSeconds = summary.path("duration").asDouble(0.0);

            double totalDistanceKm = Math.round((totalDistanceMeters / 1000.0) * 10.0) / 10.0;
            double totalDurationMinutes = Math.round(totalDurationSeconds / 60.0);

            List<List<Double>> polyline = new ArrayList<>();
            if (geometryCoords.isArray()) {
                for (JsonNode pt : geometryCoords) {
                    if (pt.isArray() && pt.size() >= 2) {
                        polyline.add(List.of(pt.get(0).asDouble(), pt.get(1).asDouble()));
                    }
                }
            }

            List<RouteStopDto> stops = new ArrayList<>();
            for (int i = 0; i < orderedPlaces.size(); i++) {
                TouristPlace p = orderedPlaces.get(i);
                double legDistanceKm = 0.0;
                double legDurationMin = 0.0;

                if (i > 0 && segments.isArray() && segments.size() >= i) {
                    JsonNode seg = segments.get(i - 1);
                    legDistanceKm = Math.round((seg.path("distance").asDouble(0.0) / 1000.0) * 10.0) / 10.0;
                    legDurationMin = Math.round(seg.path("duration").asDouble(0.0) / 60.0);
                }

                stops.add(new RouteStopDto(
                        p.getPlaceId(),
                        p.getPlaceName(),
                        p.getLocation(),
                        p.getCategory(),
                        p.getSubCategory(),
                        p.getLatitude(),
                        p.getLongitude(),
                        i + 1,
                        i == 0,
                        i == orderedPlaces.size() - 1,
                        legDistanceKm,
                        legDurationMin,
                        p.getImageUrl()
                ));
            }

            String externalNavUrl = buildGoogleMapsUrl(orderedPlaces);
            int[] eco = calculateEcoMetrics(originalMode);

            return new RouteOptimizationResponse(
                    true,
                    "Route optimized successfully along road network.",
                    stops,
                    totalDistanceKm,
                    totalDurationMinutes,
                    formatDuration(totalDurationMinutes),
                    orderedPlaces.get(0).getPlaceName(),
                    orderedPlaces.get(orderedPlaces.size() - 1).getPlaceName(),
                    profile,
                    polyline,
                    externalNavUrl,
                    eco[0],
                    getEcoMessage(originalMode)
            );
        } catch (Exception e) {
            log.error("Failed to parse OpenRouteService GeoJSON response: {}", e.getMessage(), e);
            return buildFallbackResponse(orderedPlaces, originalMode, "Error parsing routing data: " + e.getMessage());
        }
    }

    private RouteOptimizationResponse buildFallbackResponse(
            List<TouristPlace> orderedPlaces,
            String originalMode,
            String failureReason) {

        double totalDistanceKm = 0.0;
        List<RouteStopDto> stops = new ArrayList<>();
        List<List<Double>> straightLines = new ArrayList<>();

        for (int i = 0; i < orderedPlaces.size(); i++) {
            TouristPlace p = orderedPlaces.get(i);
            straightLines.add(List.of(p.getLongitude(), p.getLatitude()));

            double legDist = 0.0;
            if (i > 0) {
                legDist = Math.round(haversineDistance(orderedPlaces.get(i - 1), p) * 10.0) / 10.0;
                totalDistanceKm += legDist;
            }

            double legMin = Math.round((legDist / 40.0) * 60.0); // assume 40 km/h average
            stops.add(new RouteStopDto(
                    p.getPlaceId(),
                    p.getPlaceName(),
                    p.getLocation(),
                    p.getCategory(),
                    p.getSubCategory(),
                    p.getLatitude(),
                    p.getLongitude(),
                    i + 1,
                    i == 0,
                    i == orderedPlaces.size() - 1,
                    legDist,
                    legMin,
                    p.getImageUrl()
            ));
        }

        totalDistanceKm = Math.round(totalDistanceKm * 10.0) / 10.0;
        double totalDurationMinutes = Math.round((totalDistanceKm / 40.0) * 60.0);

        int[] eco = calculateEcoMetrics(originalMode);

        return new RouteOptimizationResponse(
                true,
                "Optimized waypoint sequence determined. Note: " + failureReason,
                stops,
                totalDistanceKm,
                totalDurationMinutes,
                formatDuration(totalDurationMinutes),
                orderedPlaces.get(0).getPlaceName(),
                orderedPlaces.get(orderedPlaces.size() - 1).getPlaceName(),
                originalMode != null ? originalMode : "car",
                straightLines,
                buildGoogleMapsUrl(orderedPlaces),
                eco[0],
                getEcoMessage(originalMode)
        );
    }

    private String buildGoogleMapsUrl(List<TouristPlace> places) {
        if (places.size() < 2) return "";
        TouristPlace origin = places.get(0);
        TouristPlace dest = places.get(places.size() - 1);

        StringBuilder sb = new StringBuilder("https://www.google.com/maps/dir/?api=1");
        sb.append("&origin=").append(origin.getLatitude()).append(",").append(origin.getLongitude());
        sb.append("&destination=").append(dest.getLatitude()).append(",").append(dest.getLongitude());

        if (places.size() > 2) {
            sb.append("&waypoints=");
            for (int i = 1; i < places.size() - 1; i++) {
                TouristPlace wp = places.get(i);
                if (i > 1) sb.append("%7C"); // URL encoded '|'
                sb.append(wp.getLatitude()).append(",").append(wp.getLongitude());
            }
        }
        return sb.toString();
    }

    private String formatDuration(double minutes) {
        long totalMins = Math.round(minutes);
        if (totalMins < 60) {
            return totalMins + " min";
        }
        long hrs = totalMins / 60;
        long rem = totalMins % 60;
        return rem == 0 ? hrs + " hrs" : hrs + " hrs " + rem + " min";
    }

    private int[] calculateEcoMetrics(String mode) {
        if (mode == null) return new int[]{45};
        return switch (mode.toLowerCase()) {
            case "walk", "walking", "foot-walking" -> new int[]{100};
            case "bike", "cycling", "cycling-regular" -> new int[]{95};
            case "transit", "bus" -> new int[]{80};
            default -> new int[]{45};
        };
    }

    private String getEcoMessage(String mode) {
        if (mode == null) return "Driving has moderate emissions.";
        return switch (mode.toLowerCase()) {
            case "walk", "walking", "foot-walking" -> "Walking produces zero carbon emissions.";
            case "bike", "cycling", "cycling-regular" -> "Cycling is eco-friendly with minimal carbon footprint.";
            case "transit", "bus" -> "Shared public transport reduces emissions per passenger.";
            default -> "Driving has moderate-to-high carbon emissions.";
        };
    }

    /**
     * Point-to-Point Shortcut Route Optimization & Comparison:
     * Calculates the primary (original) route and an alternative (shortcut) route between the exact same
     * start and final destinations, comparing road distance, travel duration, and savings honestly.
     */
    public RouteCompareResponse compareRoutes(RouteCompareRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Route comparison request cannot be null.");
        }
        if (request.getStartLat() == null || request.getStartLon() == null
                || request.getEndLat() == null || request.getEndLon() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start and end coordinates must be provided.");
        }

        double startLat = request.getStartLat();
        double startLon = request.getStartLon();
        double endLat = request.getEndLat();
        double endLon = request.getEndLon();

        if (!Double.isFinite(startLat) || !Double.isFinite(startLon)
                || !Double.isFinite(endLat) || !Double.isFinite(endLon)
                || startLat < -90 || startLat > 90 || startLon < -180 || startLon > 180
                || endLat < -90 || endLat > 90 || endLon < -180 || endLon > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coordinates are out of valid geographic range.");
        }

        if (Math.abs(startLat - endLat) < 0.0001 && Math.abs(startLon - endLon) < 0.0001) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Starting location and destination cannot be identical.");
        }

        String startName = request.getStartName() != null && !request.getStartName().isBlank()
                ? request.getStartName().trim() : "Starting Location";
        String endName = request.getEndName() != null && !request.getEndName().isBlank()
                ? request.getEndName().trim() : "Final Destination";
        String profile = resolveOrsProfile(request.getTransportMode());
        String objective = request.getPreference() != null && request.getPreference().equalsIgnoreCase("time")
                ? "time" : "distance";

        RouteCompareResponse response = new RouteCompareResponse();
        response.setStartName(startName);
        response.setStartLat(startLat);
        response.setStartLon(startLon);
        response.setEndName(endName);
        response.setEndLat(endLat);
        response.setEndLon(endLon);
        response.setTransportMode(request.getTransportMode() != null ? request.getTransportMode() : "car");
        response.setOptimizationObjective(objective);
        response.setExternalNavigationUrl("https://www.google.com/maps/dir/?api=1&origin=" + startLat + "," + startLon
                + "&destination=" + endLat + "," + endLon);

        // Try OpenRouteService first
        boolean orsSuccess = false;
        if (!apiKey.isEmpty()) {
            try {
                orsSuccess = queryOrsAlternativeRoutes(startLon, startLat, endLon, endLat, profile, objective, response);
            } catch (Exception e) {
                log.warn("OpenRouteService compare failed: {}. Falling back to OSRM.", e.getMessage());
            }
        }

        // Fallback to OSRM with alternatives=true if ORS didn't succeed
        if (!orsSuccess) {
            queryOsrmAlternativeRoutes(startLon, startLat, endLon, endLat, request.getTransportMode(), objective, response);
        }

        return response;
    }

    private boolean queryOrsAlternativeRoutes(
            double startLon, double startLat,
            double endLon, double endLat,
            String profile, String objective,
            RouteCompareResponse response) {

        String orsUrl = "https://api.openrouteservice.org/v2/directions/" + profile + "/geojson";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("coordinates", List.of(List.of(startLon, startLat), List.of(endLon, endLat)));
        requestBody.put("alternative_routes", Map.of("target_count", 3));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        String jsonResponse = restTemplate.postForObject(orsUrl, entity, String.class);

        if (jsonResponse == null || jsonResponse.isBlank()) {
            return false;
        }

        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode features = root.path("features");
            if (!features.isArray() || features.isEmpty()) {
                return false;
            }

            // Feature 0: Original route (normal / primary route)
            JsonNode primaryFeature = features.get(0);
            RouteDetailDto originalRoute = parseOrsFeature(primaryFeature, "Original Route");
            response.setOriginalRoute(originalRoute);

            // Check for alternative candidate features
            RouteDetailDto bestCandidate = null;
            for (int i = 1; i < features.size(); i++) {
                RouteDetailDto cand = parseOrsFeature(features.get(i), "Optimized Shortcut Route");
                if (isDistinctRoute(originalRoute, cand)) {
                    if (bestCandidate == null) {
                        bestCandidate = cand;
                    } else if ("time".equalsIgnoreCase(objective)) {
                        if (cand.getDurationMinutes() < bestCandidate.getDurationMinutes()) {
                            bestCandidate = cand;
                        }
                    } else { // default "distance"
                        if (cand.getDistanceKm() < bestCandidate.getDistanceKm()) {
                            bestCandidate = cand;
                        }
                    }
                }
            }

            if (bestCandidate != null) {
                response.setHasAlternative(true);
                response.setOptimizedRoute(bestCandidate);

                double distSaved = Math.round((originalRoute.getDistanceKm() - bestCandidate.getDistanceKm()) * 10.0) / 10.0;
                double timeSaved = Math.round(originalRoute.getDurationMinutes() - bestCandidate.getDurationMinutes());
                response.setDistanceSavedKm(distSaved);
                response.setTimeSavedMinutes(timeSaved);
                response.setFormattedTimeSaved(formatTimeSaved(timeSaved));

                if (distSaved > 0 && originalRoute.getDistanceKm() > 0) {
                    response.setPercentReduction(Math.round((distSaved / originalRoute.getDistanceKm()) * 1000.0) / 10.0);
                    response.setMessage("Optimized shortcut route found saving " + distSaved + " km (" + response.getPercentReduction() + "% reduction).");
                } else if (distSaved < 0) {
                    response.setPercentReduction(0.0);
                    response.setMessage("Alternative route is " + Math.abs(distSaved) + " km longer.");
                } else {
                    response.setPercentReduction(0.0);
                    response.setMessage("Alternative route found with identical road distance.");
                }
            } else {
                response.setHasAlternative(false);
                response.setOptimizedRoute(null);
                response.setDistanceSavedKm(0.0);
                response.setTimeSavedMinutes(0.0);
                response.setPercentReduction(0.0);
                response.setMessage("No alternative route is available for these locations");
            }

            response.setSuccess(true);
            return true;
        } catch (Exception e) {
            log.warn("Error parsing ORS alternative routes: {}", e.getMessage());
            return false;
        }
    }

    private RouteDetailDto parseOrsFeature(JsonNode feature, String routeName) {
        JsonNode summary = feature.path("properties").path("summary");
        double distanceMeters = summary.path("distance").asDouble(0.0);
        double durationSeconds = summary.path("duration").asDouble(0.0);

        double distKm = Math.round((distanceMeters / 1000.0) * 10.0) / 10.0;
        double durMin = Math.round(durationSeconds / 60.0);

        List<List<Double>> coords = new ArrayList<>();
        JsonNode geometryCoords = feature.path("geometry").path("coordinates");
        if (geometryCoords.isArray()) {
            for (JsonNode pt : geometryCoords) {
                if (pt.isArray() && pt.size() >= 2) {
                    coords.add(List.of(pt.get(0).asDouble(), pt.get(1).asDouble()));
                }
            }
        }

        return new RouteDetailDto(routeName, distKm, durMin, formatDuration(durMin), coords, "OpenRouteService");
    }

    private void queryOsrmAlternativeRoutes(
            double startLon, double startLat,
            double endLon, double endLat,
            String transportMode, String objective,
            RouteCompareResponse response) {

        String osrmProfile = "driving";
        if (transportMode != null) {
            switch (transportMode.toLowerCase()) {
                case "walk", "walking", "foot" -> osrmProfile = "foot";
                case "bike", "bicycle", "cycling" -> osrmProfile = "bicycle";
            }
        }

        String osrmUrl = "https://router.project-osrm.org/route/v1/" + osrmProfile + "/"
                + startLon + "," + startLat + ";" + endLon + "," + endLat
                + "?overview=full&geometries=geojson&alternatives=true";

        try {
            String json = restTemplate.getForObject(osrmUrl, String.class);
            if (json == null || json.isBlank()) {
                throw new IllegalStateException("Empty response from routing provider");
            }

            JsonNode root = objectMapper.readTree(json);
            if (!"Ok".equalsIgnoreCase(root.path("code").asText())) {
                throw new IllegalStateException(root.path("message").asText("Route calculation failed"));
            }

            JsonNode routes = root.path("routes");
            if (!routes.isArray() || routes.isEmpty()) {
                throw new IllegalStateException("No route returned");
            }

            // Route 0: Original
            RouteDetailDto originalRoute = parseOsrmRoute(routes.get(0), "Original Route");
            response.setOriginalRoute(originalRoute);

            // Candidate alternative routes
            RouteDetailDto bestCandidate = null;
            for (int i = 1; i < routes.size(); i++) {
                RouteDetailDto cand = parseOsrmRoute(routes.get(i), "Optimized Shortcut Route");
                if (isDistinctRoute(originalRoute, cand)) {
                    if (bestCandidate == null) {
                        bestCandidate = cand;
                    } else if ("time".equalsIgnoreCase(objective)) {
                        if (cand.getDurationMinutes() < bestCandidate.getDurationMinutes()) {
                            bestCandidate = cand;
                        }
                    } else {
                        if (cand.getDistanceKm() < bestCandidate.getDistanceKm()) {
                            bestCandidate = cand;
                        }
                    }
                }
            }

            if (bestCandidate != null) {
                response.setHasAlternative(true);
                response.setOptimizedRoute(bestCandidate);

                double distSaved = Math.round((originalRoute.getDistanceKm() - bestCandidate.getDistanceKm()) * 10.0) / 10.0;
                double timeSaved = Math.round(originalRoute.getDurationMinutes() - bestCandidate.getDurationMinutes());
                response.setDistanceSavedKm(distSaved);
                response.setTimeSavedMinutes(timeSaved);
                response.setFormattedTimeSaved(formatTimeSaved(timeSaved));

                if (distSaved > 0 && originalRoute.getDistanceKm() > 0) {
                    response.setPercentReduction(Math.round((distSaved / originalRoute.getDistanceKm()) * 1000.0) / 10.0);
                    response.setMessage("Optimized shortcut route found saving " + distSaved + " km (" + response.getPercentReduction() + "% reduction).");
                } else if (distSaved < 0) {
                    response.setPercentReduction(0.0);
                    response.setMessage("Alternative route is " + Math.abs(distSaved) + " km longer.");
                } else {
                    response.setPercentReduction(0.0);
                    response.setMessage("Alternative route found with identical road distance.");
                }
            } else {
                response.setHasAlternative(false);
                response.setOptimizedRoute(null);
                response.setDistanceSavedKm(0.0);
                response.setTimeSavedMinutes(0.0);
                response.setPercentReduction(0.0);
                response.setMessage("No alternative route is available for these locations");
            }
            response.setSuccess(true);
        } catch (Exception e) {
            log.error("OSRM route calculation error: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Routing services are currently unavailable: " + e.getMessage());
        }
    }

    private RouteDetailDto parseOsrmRoute(JsonNode routeNode, String routeName) {
        double distMeters = routeNode.path("distance").asDouble(0.0);
        double durSec = routeNode.path("duration").asDouble(0.0);

        double distKm = Math.round((distMeters / 1000.0) * 10.0) / 10.0;
        double durMin = Math.round(durSec / 60.0);

        List<List<Double>> coords = new ArrayList<>();
        JsonNode geometryCoords = routeNode.path("geometry").path("coordinates");
        if (geometryCoords.isArray()) {
            for (JsonNode pt : geometryCoords) {
                if (pt.isArray() && pt.size() >= 2) {
                    coords.add(List.of(pt.get(0).asDouble(), pt.get(1).asDouble()));
                }
            }
        }
        return new RouteDetailDto(routeName, distKm, durMin, formatDuration(durMin), coords, "OSRM Road Network");
    }

    private boolean isDistinctRoute(RouteDetailDto r1, RouteDetailDto r2) {
        if (r1 == null || r2 == null) return false;
        if (Math.abs(r1.getDistanceKm() - r2.getDistanceKm()) >= 0.05) return true;
        if (Math.abs(r1.getDurationMinutes() - r2.getDurationMinutes()) >= 0.5) return true;
        if (r1.getGeometry() != null && r2.getGeometry() != null) {
            if (Math.abs(r1.getGeometry().size() - r2.getGeometry().size()) > 2) return true;
        }
        return false;
    }

    private String formatTimeSaved(double minutes) {
        if (Math.abs(minutes) < 0.5) return "0 min";
        long totalMins = Math.round(Math.abs(minutes));
        String timeStr = totalMins < 60 ? totalMins + " min" : (totalMins / 60) + " hrs " + (totalMins % 60) + " min";
        return minutes > 0 ? timeStr + " saved" : timeStr + " longer";
    }
}