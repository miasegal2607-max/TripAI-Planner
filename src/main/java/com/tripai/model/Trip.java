package com.tripai.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Entity @Table(name="trips") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Trip {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false)
    @ToString.Exclude @EqualsAndHashCode.Exclude private User user;
    @Column(nullable=false) private String destination;
    @Column(name="start_date") private LocalDate startDate;
    @Column(name="num_days",nullable=false) private Integer numDays;
    private String budget;
    private String style;
    @Column(columnDefinition="TEXT") private String interests;
    @Column(name="created_at") private LocalDateTime createdAt;
    @OneToMany(mappedBy="trip",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.LAZY)
    @OrderBy("dayNumber ASC") @ToString.Exclude @EqualsAndHashCode.Exclude private List<TripDay> days;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
