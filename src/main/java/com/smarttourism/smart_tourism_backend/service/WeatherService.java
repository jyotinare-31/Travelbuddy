
package com.smarttourism.smart_tourism_backend.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${openweather.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getWeather(String city) {

        String targetCity = (city != null && !city.trim().isEmpty())
                ? city.trim()
                : "Solapur";

        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException(
                    "OpenWeather API key is not configured. Please set OPENWEATHER_API_KEY."
            );
        }

        String encodedCity = URLEncoder.encode(
                targetCity,
                StandardCharsets.UTF_8
        );

        String url = "https://api.openweathermap.org/data/2.5/weather"
                + "?q=" + encodedCity
                + "&appid=" + apiKey.trim()
                + "&units=metric";

        try {
            System.out.println("Calling OpenWeather for city: " + targetCity);

            return restTemplate.getForObject(url, String.class);

        } catch (Exception e) {
            System.out.println("========== WEATHER ERROR ==========");
            System.out.println("Error fetching weather for "
                    + targetCity + ": " + e.getMessage());
            System.out.println("===================================");

            throw e;
        }
    }
}
