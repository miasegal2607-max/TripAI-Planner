package com.tripai.repository;
import com.tripai.model.TripDay;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TripDayRepository extends JpaRepository<TripDay,Long> {
    List<TripDay> findByTripIdOrderByDayNumberAsc(Long tripId);
}
