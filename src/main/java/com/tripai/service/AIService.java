package com.tripai.service;
import com.fasterxml.jackson.databind.*;
import com.tripai.model.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;
@Service @RequiredArgsConstructor @Slf4j
public class AIService {
    @Value("${app.openai.api-key}") private String apiKey;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private static final String URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o-mini";

    public void generateItinerary(Trip trip, List<TripDay> days, List<List<Place>> placesPerDay) {
        String prompt = String.format("Create a detailed %d-day itinerary for %s. Budget: %s. Style: %s. Interests: %s.\nReturn ONLY valid JSON: {\"days\":[{\"dayNumber\":1,\"summary\":\"...\",\"places\":[{\"name\":\"\",\"address\":\"\",\"placeType\":\"attraction|restaurant\",\"visitTime\":\"09:00\",\"visitOrder\":1,\"rating\":4.5,\"priceLevel\":\"$$\",\"openingHours\":\"09:00-18:00\",\"website\":\"\",\"mapsUrl\":\"\",\"notes\":\"\"}]}]}",
                trip.getNumDays(), trip.getDestination(),
                Objects.toString(trip.getBudget(),"medium"), Objects.toString(trip.getStyle(),"cultural"), Objects.toString(trip.getInterests(),"sightseeing"));
        String raw = callOpenAI(prompt, true);
        try {
            JsonNode root = objectMapper.readTree(raw);
            for (JsonNode d : root.path("days")) {
                TripDay day = TripDay.builder().trip(trip).dayNumber(d.path("dayNumber").asInt()).summary(d.path("summary").asText()).build();
                days.add(day);
                List<Place> places = new ArrayList<>();
                for (JsonNode p : d.path("places")) {
                    places.add(Place.builder().name(p.path("name").asText()).address(p.path("address").asText("")).placeType(p.path("placeType").asText("attraction"))
                            .visitTime(p.path("visitTime").asText("")).visitOrder(p.path("visitOrder").asInt(places.size()+1))
                            .rating(p.has("rating")?p.path("rating").asDouble():null).priceLevel(p.path("priceLevel").asText(""))
                            .openingHours(p.path("openingHours").asText("")).website(p.path("website").asText(""))
                            .mapsUrl(p.path("mapsUrl").asText("")).notes(p.path("notes").asText("")).build());
                }
                placesPerDay.add(places);
            }
        } catch(Exception e) { throw new RuntimeException("Failed to parse AI response"); }
    }

    public String chat(Trip trip, String message) {
        String sys = String.format("You are a travel assistant for a %d-day trip to %s (budget: %s, style: %s). Help modify or improve the itinerary.",
                trip.getNumDays(), trip.getDestination(), Objects.toString(trip.getBudget(),"medium"), Objects.toString(trip.getStyle(),"cultural"));
        Map<String,Object> body = new HashMap<>();
        body.put("model", MODEL);
        body.put("messages", List.of(Map.of("role","system","content",sys), Map.of("role","user","content",message)));
        try {
            String resp = webClientBuilder.build().post().uri(URL)
                    .header("Authorization","Bearer "+apiKey).header("Content-Type","application/json")
                    .bodyValue(body).retrieve().bodyToMono(String.class).block();
            return objectMapper.readTree(resp).path("choices").get(0).path("message").path("content").asText();
        } catch(Exception e) { throw new RuntimeException("AI service unavailable"); }
    }

    private String callOpenAI(String content, boolean jsonMode) {
        Map<String,Object> body = new HashMap<>();
        body.put("model", MODEL);
        body.put("messages", List.of(Map.of("role","user","content",content)));
        if (jsonMode) body.put("response_format", Map.of("type","json_object"));
        try {
            String resp = webClientBuilder.build().post().uri(URL)
                    .header("Authorization","Bearer "+apiKey).header("Content-Type","application/json")
                    .bodyValue(body).retrieve().bodyToMono(String.class).block();
            return objectMapper.readTree(resp).path("choices").get(0).path("message").path("content").asText();
        } catch(Exception e) { throw new RuntimeException("AI unavailable: "+e.getMessage()); }
    }
}
