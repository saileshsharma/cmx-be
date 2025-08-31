package com.cb.th.claims.cmx.entity.policy;

import com.cb.th.claims.cmx.entity.fnol.FNOL;
import com.cb.th.claims.cmx.entity.insured.Insured;
import com.cb.th.claims.cmx.entity.vehicle.Vehicle;
import com.cb.th.claims.cmx.enums.policy.PaymentFrequency;
import com.cb.th.claims.cmx.enums.policy.PaymentMethod;
import com.cb.th.claims.cmx.enums.policy.PolicyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "policy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String policyNumber;
    private String coverageType;

    @Enumerated(EnumType.STRING)          // << IMPORTANT
    @Column(name = "policy_status", nullable = false)
    private PolicyStatus policyStatus;


    private String lob;
    private LocalDate startDate;
    private LocalDate endDate;
    private String policyType; // TYPE-1, TYPE-2, TYPE-3, TYPE-4

    private Double sumInsured;
    private Double premium;



    @ManyToOne
    @JoinColumn(name = "insured_id")
    private Insured insured;

    @OneToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;



    private LocalDate issueDate;

    private LocalDate renewalDate;


    private LocalDate cancelDate;


    private LocalDate lapseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_frequency", length = 20)
    private PaymentFrequency paymentFrequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;





}