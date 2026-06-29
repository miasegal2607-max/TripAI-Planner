package com.tripai.model;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.*;
@Entity @Table(name="users") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User implements UserDetails {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(unique=true,nullable=false) private String email;
    @Column(nullable=false) private String password;
    @Column(nullable=false) private String name;
    @Column(name="created_at") private LocalDateTime createdAt;
    @OneToMany(mappedBy="user",cascade=CascadeType.ALL,orphanRemoval=true)
    @ToString.Exclude @EqualsAndHashCode.Exclude private List<Trip> trips;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
