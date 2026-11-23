package com.smarttourism.smart_tourism_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FlightService {

    @Value("${aviationstack.api.key:${AVIATIONSTACK_API_KEY:}}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getFlights(String depIata, String arrIata) {

        String url = "https://api.aviationstack.com/v1/flights"
                + "?access_key=" + apiKey
                + "&dep_iata=" + depIata
                + "&arr_iata=" + arrIata;

        return restTemplate.getForObject(url, String.class);
    }
}