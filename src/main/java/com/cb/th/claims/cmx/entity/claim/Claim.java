package com.cb.th.claims.cmx.entity.claim;

import com.cb.th.claims.cmx.entity.address.Address;
import com.cb.th.claims.cmx.entity.surveyor.Surveyor;
import com.cb.th.claims.cmx.entity.fnol.FNOL;
import com.cb.th.claims.cmx.entity.policy.Policy;
import com.cb.th.claims.cmx.enums.claim.ClaimSeverity;
import com.cb.th.claims.cmx.enums.claim.ClaimStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;


@Slf4j
@Entity
@Table(name = "claim")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String claimNumber;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private Double claimAmount;

    @ManyToOne
    @JoinColumn(name = "fnol_id")
    private FNOL fnol;

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;


    @Enumerated(EnumType.STRING)
    private ClaimStatus claimStatus;


    private LocalDate incidentDate;
    private LocalDate claimDate;
    private LocalDate dateReported;

    @Enumerated(EnumType.STRING)
    private ClaimSeverity claimSeverity;

    private String location;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surveyor_id")     // << IMPORTANT: points to Surveyor.id
    private Surveyor surveyor;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

}