package com.smarttourism.smart_tourism_backend.service;

import com.smarttourism.smart_tourism_backend.entity.TouristPlace;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiServiceTest {

    @Test
    void sendsChatMessageAndReturnsGeneratedText() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        GeminiService service = new GeminiService(
                "test-key",
                "https://generativelanguage.googleapis.com/v1beta",
                restTemplate,
                new ObjectMapper()
        );
        String responseBody = """
                {"steps":[{"type":"model_output","content":[{"type":"text","text":"Visit Solapur in winter."}]}]}
                """;

        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/interactions"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andExpect(content().string(containsString("Suggest a trip")))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        assertEquals(
                "Visit Solapur in winter.",
                service.getChatResponse(
                        "Suggest a trip",
                        List.of(new GeminiService.AiChatMessage("user", "Suggest a trip"))
                )
        );
        server.verify();
    }

    @Test
    void recommendsOnlyDatabasePlaceIdsAndReturnsDatabaseFieldsForSelection() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        GeminiService service = new GeminiService(
                "test-key",
                "https://generativelanguage.googleapis.com/v1beta",
                restTemplate,
                new ObjectMapper()
        );
        TouristPlace place = new TouristPlace();
        place.setPlaceId(54);
        place.setPlaceName("Radha Krishna Temple");
        place.setCategory("Temple");
        place.setSubCategory("Krishna Temple");
        place.setDescription("Database description");
        place.setRating(4.5);
        place.setReviewCount(7);
        place.setTicketPrice(0.0);
        place.setLocation("Solapur");
        place.setImageUrl("/images/RadhaKrishnaTemple.webp");
        place.setIsHidden(true);

        String responseBody = """
                {"steps":[{"type":"model_output","content":[{"type":"text","text":"[{\\"placeId\\":54,\\"reason\\":\\"Matches your cultural interest.\\"},{\\"placeId\\":999,\\"reason\\":\\"Invented destination.\\"},{\\"placeId\\":54,\\"reason\\":\\"Duplicate.\\"}]"}]}]}
                """;
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/interactions"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andExpect(content().string(containsString("Radha Krishna Temple")))
                .andExpect(content().string(containsString("Database description")))
                .andExpect(content().string(containsString("ticketPrice")))
                .andExpect(content().string(containsString("hidden")))
                .andExpect(content().string(containsString("Minimum rating: 4.0")))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        assertEquals(
                List.of(new GeminiService.PlaceRecommendation(54, "Matches your cultural interest.")),
                service.recommendPlaces(
                        List.of(place),
                        "Solapur",
                        500,
                        2,
                        "Walking",
                        List.of("Culture"),
                        "Temple",
                        4
                )
        );
        server.verify();
    }

    @Test
    void mapsInvalidApiKeyToActionableGatewayError() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        GeminiService service = new GeminiService(
                "bad-key",
                "https://generativelanguage.googleapis.com/v1beta",
                restTemplate,
                new ObjectMapper()
        );
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/interactions"))
                .andRespond(MockRestResponseCreators.withUnauthorizedRequest());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, service::testGemini);

        assertEquals(502, exception.getStatusCode().value());
        org.junit.jupiter.api.Assertions.assertTrue(exception.getReason().contains("Verify GEMINI_API_KEY"));
        server.verify();
    }

    @Test
    void preservesGeminiQuotaErrorForRecommendationRequests() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        GeminiService service = new GeminiService(
                "test-key",
                "https://generativelanguage.googleapis.com/v1beta",
                restTemplate,
                new ObjectMapper()
        );
        TouristPlace place = new TouristPlace();
        place.setPlaceId(54);
        place.setPlaceName("Radha Krishna Temple");
        place.setCategory("Temple");
        place.setRating(4.5);
        place.setTicketPrice(0.0);
        place.setLocation("Solapur");

        server.expect(manyTimes(), requestTo("https://generativelanguage.googleapis.com/v1beta/interactions"))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.TOO_MANY_REQUESTS)
                        .body("{\"error\":{\"message\":\"Free tier daily quota exhausted\"}}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.recommendPlaces(
                        List.of(place),
                        "Solapur",
                        500,
                        1,
                        "Walking",
                        List.of("Culture"),
                        "Temple",
                        0
                )
        );

        assertEquals(503, exception.getStatusCode().value());
        org.junit.jupiter.api.Assertions.assertTrue(exception.getReason().contains("quota or rate limit"));
        server.verify();
    }

    @Test
    void failsClearlyWhenApiKeyIsMissing() {
        GeminiService service = new GeminiService(
                "",
                "https://generativelanguage.googleapis.com/v1beta",
                new RestTemplate(),
                new ObjectMapper()
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                service::testGemini
        );

        assertEquals(503, exception.getStatusCode().value());
    }
}
