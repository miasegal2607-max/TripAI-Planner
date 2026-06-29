package com.tripai.dto.response;
import lombok.*;
@Data @Builder public class PlaceDto {
    private Long id;
    private String name, address, openingHours, priceLevel, website, mapsUrl, placeType, visitTime, notes;
    private Double rating, latitude, longitude;
    private Integer visitOrder;
}
