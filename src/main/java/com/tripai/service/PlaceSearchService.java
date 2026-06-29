package com.tripai.service;
import com.fasterxml.jackson.databind.*;
import com.tripai.dto.response.PlaceDto;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.*;
@Service @RequiredArgsConstructor @Slf4j
public class PlaceSearchService {
    @Value("${app.google.places-api-key}") private String apiKey;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    public List<PlaceDto> searchPlaces(String query, String location) {
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/place/textsearch/json")
                .queryParam("query", query+" in "+location).queryParam("key",apiKey).toUriString();
        try {
            String resp = webClientBuilder.build().get().uri(url).retrieve().bodyToMono(String.class).block();
            List<PlaceDto> results = new ArrayList<>();
            for (JsonNode r : objectMapper.readTree(resp).path("results")) {
                JsonNode loc = r.path("geometry").path("location");
                results.add(PlaceDto.builder().name(r.path("name").asText()).address(r.path("formatted_address").asText(""))
                        .rating(r.has("rating")?r.path("rating").asDouble():null)
                        .latitude(loc.path("lat").asDouble()).longitude(loc.path("lng").asDouble())
                        .mapsUrl("https://maps.google.com/?q="+r.path("name").asText("").replace(" ","+")).build());
            }
            return results;
        } catch(Exception e) { log.error("Google Places error",e); return List.of(); }
    }
}
