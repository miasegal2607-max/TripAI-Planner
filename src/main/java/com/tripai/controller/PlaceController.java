package com.tripai.controller;
import com.tripai.dto.response.PlaceDto;
import com.tripai.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/places") @RequiredArgsConstructor
public class PlaceController {
    private final PlaceSearchService placeSearchService;
    private final WeatherService weatherService;
    @GetMapping("/search") public ResponseEntity<List<PlaceDto>> search(@RequestParam String query, @RequestParam String location) { return ResponseEntity.ok(placeSearchService.searchPlaces(query,location)); }
    @GetMapping("/weather") public ResponseEntity<Map<String,Object>> weather(@RequestParam String city) { return ResponseEntity.ok(weatherService.getWeather(city)); }
}
