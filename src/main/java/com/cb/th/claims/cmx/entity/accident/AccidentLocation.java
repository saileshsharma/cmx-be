package com.cb.th.claims.cmx.entity.accident;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "accident_location")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AccidentLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "formatted_address")
    private String formattedAddress;

    @Column(name = "google_place_id")
    private String googlePlaceId;

    @Column(name = "location_type")
    private String locationType; // e.g., ACCIDENT_SITE

    private String city;
    private String province;

    @Column(name = "postal_code")
    private String postalCode;

    private String country;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.locationType == null || this.locationType.isBlank()) this.locationType = "ACCIDENT_SITE";
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

