package com.smarttourism.smart_tourism_backend.service;

import com.smarttourism.smart_tourism_backend.entity.TouristPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.GATEWAY_TIMEOUT;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Service
public class GeminiService {

    private final String geminiApiKey;
    private final String baseUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final List<String> MODELS = List.of(
            "gemini-3.5-flash-lite",
            "gemini-3.1-flash-lite",
            "gemini-3.8-flash"
    );

    @Autowired
    public GeminiService(
            @Value("${gemini.api.key:${GEMINI_API_KEY:}}") String geminiApiKey,
            @Value("${gemini.api.base-url:https://generativelanguage.googleapis.com/v1beta}") String baseUrl,
            ObjectMapper objectMapper) {
        this(geminiApiKey, baseUrl, createRestTemplate(), objectMapper);
    }

    GeminiService(String geminiApiKey, String baseUrl, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.geminiApiKey = geminiApiKey;
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    private static RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(90));
        return new RestTemplate(requestFactory);
    }

    // =========================================================
    // TEST GEMINI API
    // =========================================================

    public String testGemini() {

        String prompt =
                "Reply with exactly: Gemini API is working.";

        return extractGeneratedText(callGemini(prompt));
    }


    public List<PlaceRecommendation> recommendPlaces(
            List<TouristPlace> places,
            String location,
            double budget,
            int days,
            String travelPreference,
            List<String> interests,
            String preferredCategory,
            double minimumRating) {
        return recommendPlaces(places, location, budget, days, travelPreference, interests, preferredCategory, minimumRating, "en");
    }

    public List<PlaceRecommendation> recommendPlaces(
            List<TouristPlace> places,
            String location,
            double budget,
            int days,
            String travelPreference,
            List<String> interests,
            String preferredCategory,
            double minimumRating,
            String language) {
        List<RecommendationCandidate> candidates = places.stream()
                .map(place -> new RecommendationCandidate(
                        place.getPlaceId(),
                        place.getPlaceName(),
                        place.getCategory(),
                        place.getSubCategory(),
                        place.getDescription(),
                        place.getRating(),
                        place.getReviewCount(),
                        place.getTicketPrice(),
                        place.getLocation(),
                        place.getIsHidden()
                ))
                .toList();

        String databasePlaces;
        try {
            databasePlaces = objectMapper.writeValueAsString(candidates);
        } catch (Exception exception) {
            throw new ResponseStatusException(BAD_GATEWAY, "Unable to prepare database places for Gemini", exception);
        }

        String langInstruction = "";
        if (language != null) {
            if ("mr".equalsIgnoreCase(language) || "marathi".equalsIgnoreCase(language)) {
                langInstruction = "\nLanguage instruction: Provide the personalized reason string and speciality in Marathi language (मराठी). Keep destination place names recognizable.";
            } else if ("hi".equalsIgnoreCase(language) || "hindi".equalsIgnoreCase(language)) {
                langInstruction = "\nLanguage instruction: Provide the personalized reason string and speciality in Hindi language (हिंदी). Keep destination place names recognizable.";
            }
        }

        int recommendationLimit = Math.min(12, days * 2);
        String prompt = """
                Recommend up to %d tourist places for this trip using ONLY the supplied PostgreSQL place records.
                The records are the complete allowed destination list. Never invent, rename, or add a place.
                Select places that best match the traveler interests, budget, preferred category, rating threshold,
                trip duration, and travel preference. Avoid duplicate place IDs.
                Return only a JSON array. Each item must contain exactly an integer placeId from the supplied
                records and a short personalized reason string. Do not include or infer prices, ratings, locations,
                descriptions, image URLs, or any other facts in the reason.%s

                TRAVELER PREFERENCES
                Starting location: %s
                Total budget in INR: %.2f
                Trip duration in days: %d
                Travel preference: %s
                Interests: %s
                Preferred category: %s
                Minimum rating: %.1f out of 5

                DATABASE PLACE RECORDS
                %s

                Required JSON format:
                [{"placeId": 123, "reason": "Matches your interest in history."}]
                """.formatted(
                recommendationLimit,
                langInstruction,
                location,
                budget,
                days,
                travelPreference,
                String.join(", ", interests),
                preferredCategory.isBlank() ? "Any" : preferredCategory,
                minimumRating,
                databasePlaces
        );

        JsonNode recommendations;
        try {
            String output = extractGeneratedText(callGemini(prompt))
                    .replaceFirst("^```(?:json)?\\s*", "")
                    .replaceFirst("\\s*```$", "")
                    .trim();
            JsonNode root = objectMapper.readTree(output);
            recommendations = root.isArray() ? root : root.path("recommendations");
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ResponseStatusException(BAD_GATEWAY, "Gemini returned invalid recommendation JSON", exception);
        }

        Set<Integer> allowedPlaceIds = new HashSet<>();
        places.forEach(place -> allowedPlaceIds.add(place.getPlaceId()));
        Set<Integer> returnedPlaceIds = new HashSet<>();
        List<PlaceRecommendation> result = new ArrayList<>();
        for (JsonNode recommendation : recommendations) {
            JsonNode placeIdNode = recommendation.path("placeId");
            JsonNode reasonNode = recommendation.path("reason");
            JsonNode specialityNode = recommendation.path("speciality");
            if (!placeIdNode.isIntegralNumber() || !reasonNode.isString()) {
                continue;
            }
            int placeId = placeIdNode.intValue();
            String reason = reasonNode.stringValue().trim();
            String speciality = specialityNode.isString() ? specialityNode.stringValue().trim() : "";
            if (allowedPlaceIds.contains(placeId) && returnedPlaceIds.add(placeId) && !reason.isBlank()) {
                result.add(new PlaceRecommendation(placeId, reason, speciality));
                if (result.size() >= recommendationLimit) {
                    break;
                }
            }
        }
        return List.copyOf(result);
    }

    private record RecommendationCandidate(
            Integer placeId,
            String placeName,
            String category,
            String subCategory,
            String description,
            Double rating,
            Integer reviewCount,
            Double ticketPrice,
            String location,
            Boolean hidden
    ) {
    }

    public record PlaceRecommendation(Integer placeId, String reason, String speciality) {
        public PlaceRecommendation(Integer placeId, String reason) {
            this(placeId, reason, "");
        }
    }

    public String getChatResponse(String message, List<AiChatMessage> history) {
        if (message == null || message.isBlank() || message.length() > 4000) {
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Message must contain between 1 and 4000 characters"
            );
        }

        StringBuilder prompt = new StringBuilder(
                "You are TravelBuddy, a tourism assistant for Solapur District, Maharashtra, India. "
                        + "Give concise, practical travel advice and answer the traveler's actual question. "
                        + "When the traveler communicates in or asks for Marathi or Hindi, provide your response in that respective language (मराठी or हिंदी). "
                        + "When the traveler asks about Solapur, recommend relevant local places such as "
                        + "Bhuikot Fort, Siddheshwar Temple, Pandharpur, Akkalkot, Karmala, Ujjani Dam, "
                        + "or Kambar Lake; never substitute an unrelated destination outside Solapur. "
                        + "Be clear when prices or details are estimates.\n"
        );
        if (history != null && !history.isEmpty()) {
            int end = history.size();
            AiChatMessage lastEntry = history.get(end - 1);
            if (lastEntry != null && "user".equalsIgnoreCase(lastEntry.role())
                    && message.equals(lastEntry.content())) {
                end--;
            }
            int start = Math.max(0, end - 10);
            for (AiChatMessage entry : history.subList(start, end)) {
                if (entry == null || entry.content() == null || entry.content().isBlank()) {
                    continue;
                }
                String role = switch (entry.role() == null ? "" : entry.role().toLowerCase()) {
                    case "user" -> "Traveler";
                    case "assistant", "model" -> "TravelBuddy";
                    default -> throw new ResponseStatusException(
                            org.springframework.http.HttpStatus.BAD_REQUEST,
                            "Chat history roles must be user or assistant"
                    );
                };
                String content = entry.content();
                prompt.append(role).append(": ")
                        .append(content, 0, Math.min(content.length(), 4000))
                        .append('\n');
            }
        }
        prompt.append("Traveler: ").append(message).append("\nTravelBuddy:");
        return extractGeneratedText(callGemini(prompt.toString()));
    }

    private String extractGeneratedText(String responseBody) {
        try {
            JsonNode interaction = objectMapper.readTree(responseBody);
            JsonNode outputText = interaction.path("output_text");
            if (outputText.isString() && !outputText.stringValue().isBlank()) {
                return outputText.stringValue().trim();
            }

            StringBuilder generatedText = new StringBuilder();
            for (JsonNode step : interaction.path("steps")) {
                if ("model_output".equals(step.path("type").stringValue())) {
                    for (JsonNode part : step.path("content")) {
                        if ("text".equals(part.path("type").stringValue()) && part.hasNonNull("text")) {
                            if (!generatedText.isEmpty()) {
                                generatedText.append('\n');
                            }
                            generatedText.append(part.path("text").stringValue());
                        }
                    }
                }
            }
            if (!generatedText.isEmpty()) {
                return generatedText.toString().trim();
            }
        } catch (Exception exception) {
            throw new ResponseStatusException(BAD_GATEWAY, "Gemini returned an invalid response", exception);
        }
        throw new ResponseStatusException(BAD_GATEWAY, "Gemini returned no text content");
    }

    // =========================================================
    // GEMINI INTERACTIONS API CALL
    // =========================================================

    private String callGemini(String input) {

        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Gemini API key is not configured");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", geminiApiKey.trim());
        String url = baseUrl.replaceAll("/+$", "") + "/interactions";

        ResponseStatusException lastException = null;

        for (String modelName : MODELS) {
            Map<String, Object> requestBody = Map.of(
                    "model", modelName,
                    "input", input,
                    "generation_config", Map.of("thinking_level", "low"),
                    "store", false
            );
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            for (int attempt = 1; attempt <= 2; attempt++) {
                try {
                    String response = restTemplate.postForObject(url, request, String.class);
                    if (response != null && !response.isBlank()) {
                        return response;
                    }
                } catch (HttpStatusCodeException exception) {
                    int statusCode = exception.getStatusCode().value();
                    if (statusCode == 401 || statusCode == 403) {
                        throw new ResponseStatusException(
                                BAD_GATEWAY,
                                "Gemini rejected the API key (HTTP " + statusCode
                                        + "). Verify GEMINI_API_KEY and enable the Gemini API for its project."
                        );
                    }
                    if (statusCode == 404) {
                        lastException = new ResponseStatusException(
                                BAD_GATEWAY,
                                "Gemini model " + modelName + " is unavailable for this API key or project. "
                                        + "Verify Gemini API access and model availability."
                        );
                        break;
                    }
                    if (statusCode >= 500 && attempt == 1) {
                        continue;
                    }
                    if (statusCode == 429) {
                        lastException = new ResponseStatusException(
                                SERVICE_UNAVAILABLE,
                                "Gemini quota or rate limit reached (HTTP 429). Check project quota and billing, then retry."
                        );
                        break;
                    }
                    if (statusCode >= 500) {
                        lastException = new ResponseStatusException(
                                SERVICE_UNAVAILABLE,
                                "Gemini is temporarily unavailable (HTTP " + statusCode + "). Retry shortly."
                        );
                        break;
                    }
                    throw new ResponseStatusException(
                            BAD_GATEWAY,
                            "Gemini rejected the request with HTTP " + statusCode
                    );
                } catch (ResourceAccessException exception) {
                    throw new ResponseStatusException(
                            GATEWAY_TIMEOUT,
                            "Timed out connecting to the Gemini API. Check internet access and retry.",
                            exception
                    );
                }
            }
        }

        if (lastException != null) {
            throw lastException;
        }
        throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Gemini is temporarily unavailable");
    }

    public record AiChatMessage(String role, String content) {
    }
}