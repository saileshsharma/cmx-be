package com.cb.th.claims.cmx.entity.surveyor;


import com.cb.th.claims.cmx.entity.fnol.FNOL;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorJobStatus;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "surveyor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Surveyor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phoneNumber;

    private Double currentLat;
    private Double currentLng;

    private int ratingAvg;
    private String appVersion;

    private String capacityPerDay;

    private int activeJobsCount;

    private String skills;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    private String city;
    private String province;
    private String country;
    private Boolean isActive;


    @Column(nullable = false)               // keep schema constraint
    private boolean internal;       // JVM default for existing rows

    @Enumerated(EnumType.STRING)
    private SurveyorStatus status;  // Surveyor's availability


    @Enumerated(EnumType.STRING)
    private SurveyorJobStatus surveyorJobStatus;  // Surveyor's availability


    @ManyToOne
    @JoinColumn(name = "fnol_id")
    private FNOL fnol;

}
