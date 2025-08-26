package com.cb.th.claims.cmx.entity.fnol;

import com.cb.th.claims.cmx.entity.address.Address;
import com.cb.th.claims.cmx.entity.policy.Policy;
import com.cb.th.claims.cmx.entity.surveyor.Surveyor;
import com.cb.th.claims.cmx.entity.vehicle.Vehicle;
import com.cb.th.claims.cmx.enums.claim.ClaimSeverity;
import com.cb.th.claims.cmx.enums.fnol.FNOLState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fnol")
@Getter
@Setter
@NoArgsConstructor
public class FNOL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "incident_description")
    private String incidentDescription;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private ClaimSeverity severity; // High | Medium | Low

    @Column(name = "accident_date")
    private LocalDateTime accidentDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(optional = true)             // <- allow null
    @JoinColumn(name = "surveyor_id")       // no nullable=false
    private Surveyor surveyor;

    // If DB already has a UNIQUE index on accident_location_id, you can omit 'unique = true' here.
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accident_location_id", nullable = false)
    private Address accidentLocation;

    private LocalDate updatedAt;

    private String fnolReferenceNo;

    @Enumerated(EnumType.STRING)
    private FNOLState fnolState;
}
