package com.tripai.controller;
import com.tripai.dto.request.ChatRequest;
import com.tripai.model.*;
import com.tripai.repository.TripRepository;
import com.tripai.service.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/ai") @RequiredArgsConstructor
public class AIController {
    private final AIService aiService;
    private final TripRepository tripRepository;
    @PostMapping("/chat") public ResponseEntity<Map<String,String>> chat(@Valid @RequestBody ChatRequest r, @AuthenticationPrincipal User u) {
        Trip trip = tripRepository.findByIdAndUserId(r.getTripId(),u.getId()).orElseThrow(()->new IllegalArgumentException("Trip not found"));
        return ResponseEntity.ok(Map.of("reply", aiService.chat(trip, r.getMessage())));
    }
}
