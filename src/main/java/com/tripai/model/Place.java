package com.tripai.model;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="places") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Place {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="trip_day_id",nullable=false)
    @ToString.Exclude @EqualsAndHashCode.Exclude private TripDay tripDay;
    @Column(nullable=false) private String name;
    private String address;
    private Double rating;
    @Column(name="opening_hours") private String openingHours;
    @Column(name="price_level") private String priceLevel;
    private String website;
    @Column(name="maps_url") private String mapsUrl;
    @Column(name="place_type") private String placeType;
    @Column(name="visit_order") private Integer visitOrder;
    @Column(name="visit_time") private String visitTime;
    private Double latitude;
    private Double longitude;
    @Column(name="google_place_id") private String googlePlaceId;
    @Column(columnDefinition="TEXT") private String notes;
}
