package com.tripai.repository;
import com.tripai.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface TripRepository extends JpaRepository<Trip,Long> {
    List<Trip> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Trip> findByIdAndUserId(Long id, Long userId);
}
