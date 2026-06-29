package com.tripai.controller;
import com.tripai.dto.request.CreateTripRequest;
import com.tripai.dto.response.TripResponse;
import com.tripai.model.User;
import com.tripai.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/trips") @RequiredArgsConstructor
public class TripController {
    private final TripService tripService;
    @PostMapping public ResponseEntity<TripResponse> create(@Valid @RequestBody CreateTripRequest r, @AuthenticationPrincipal User u) { return ResponseEntity.ok(tripService.createTrip(r,u)); }
    @GetMapping public ResponseEntity<List<TripResponse>> list(@AuthenticationPrincipal User u) { return ResponseEntity.ok(tripService.getUserTrips(u.getId())); }
    @GetMapping("/{id}") public ResponseEntity<TripResponse> get(@PathVariable Long id, @AuthenticationPrincipal User u) { return ResponseEntity.ok(tripService.getTrip(id,u.getId())); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User u) { tripService.deleteTrip(id,u.getId()); return ResponseEntity.noContent().build(); }
}
