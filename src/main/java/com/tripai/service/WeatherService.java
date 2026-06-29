package com.tripai.service;
import com.fasterxml.jackson.databind.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.*;
@Service @RequiredArgsConstructor @Slf4j
public class WeatherService {
    @Value("${app.openweather.api-key}") private String apiKey;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    public Map<String,Object> getWeather(String city) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("q",city).queryParam("appid",apiKey).queryParam("units","metric").toUriString();
        try {
            String resp = webClientBuilder.build().get().uri(url).retrieve().bodyToMono(String.class).block();
            JsonNode root = objectMapper.readTree(resp);
            return Map.of("city",root.path("name").asText(),"temp",root.path("main").path("temp").asDouble(),
                    "description",root.path("weather").get(0).path("description").asText(),
                    "humidity",root.path("main").path("humidity").asInt());
        } catch(Exception e) { return Map.of("error","Weather unavailable"); }
    }
}
