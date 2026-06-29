package com.tripai.dto.response;
import lombok.*;
import java.time.*;
import java.util.List;
@Data @Builder public class TripResponse {
    private Long id;
    private String destination, budget, style, interests;
    private Integer numDays;
    private LocalDate startDate;
    private LocalDateTime createdAt;
    private List<TripDayDto> days;
}
