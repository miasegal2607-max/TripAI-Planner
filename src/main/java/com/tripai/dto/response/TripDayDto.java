package com.tripai.dto.response;
import lombok.*;
import java.util.List;
@Data @Builder public class TripDayDto {
    private Long id;
    private Integer dayNumber;
    private String summary;
    private List<PlaceDto> places;
}
