package com.tripai.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
@Entity @Table(name="trip_days") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TripDay {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="trip_id",nullable=false)
    @ToString.Exclude @EqualsAndHashCode.Exclude private Trip trip;
    @Column(name="day_number",nullable=false) private Integer dayNumber;
    @Column(columnDefinition="TEXT") private String summary;
    @OneToMany(mappedBy="tripDay",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.LAZY)
    @OrderBy("visitOrder ASC") @ToString.Exclude @EqualsAndHashCode.Exclude private List<Place> places;
}
