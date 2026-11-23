package com.smarttourism.smart_tourism_backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class HotelbedsServiceTest {

    @Test
    void requestsLiveRatesForTheRequestedStayAndDestination() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        HotelbedsService service = new HotelbedsService(
                "test-key",
                "test-secret",
                "https://api.hotelbeds.com",
                restTemplate
        );
        String responseBody = "{\"hotels\":{\"hotels\":[]}}";

        server.expect(requestTo("https://api.hotelbeds.com/hotel-api/1.0/hotels"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Api-Key", "test-key"))
                .andExpect(header("X-Signature", org.hamcrest.Matchers.matchesPattern("[a-f0-9]{64}")))
                .andExpect(content().json("""
                        {
                          "stay": {"checkIn": "2026-11-10", "checkOut": "2026-11-12"},
                          "occupancies": [{"rooms": 1, "adults": 2, "children": 0}],
                          "destination": {"code": "BOM"}
                        }
                        """))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        assertEquals(
                responseBody,
                service.getLiveHotelRates(
                        "BOM",
                        LocalDate.of(2026, 11, 10),
                        LocalDate.of(2026, 11, 12),
                        1,
                        2
                )
        );
        server.verify();
    }

    @Test
    void rejectsCheckoutDateThatIsNotAfterCheckin() {
        HotelbedsService service = new HotelbedsService(
                "test-key",
                "test-secret",
                "https://api.hotelbeds.com",
                new RestTemplate()
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.getLiveHotelRates(
                        "BOM",
                        LocalDate.of(2026, 11, 12),
                        LocalDate.of(2026, 11, 10),
                        1,
                        2
                )
        );

        assertEquals(400, exception.getStatusCode().value());
    }
}
