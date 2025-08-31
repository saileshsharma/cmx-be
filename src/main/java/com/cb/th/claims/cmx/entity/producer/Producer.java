package com.cb.th.claims.cmx.entity.producer;



import com.cb.th.claims.cmx.enums.producer.ProducerStatus;
import com.cb.th.claims.cmx.enums.producer.ProducerType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "producer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producer_code", nullable = false, unique = true, length = 50)
    private String producerCode;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    private LocalDate licenseExpiry;

    @Enumerated(EnumType.STRING)
    @Column(name = "producer_type", nullable = false, length = 20)
    private ProducerType producerType;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(length = 150)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(name = "address_id")
    private Long addressId;   // FK to Address table

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProducerStatus status = ProducerStatus.ACTIVE;

    @Column(name = "join_date")
    private LocalDate joinDate = LocalDate.now();

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "supervisor_id")
    private Long supervisorId;  // self-reference

    @Column(name = "commission_rate")
    private Double commissionRate = 0.0;

    @Column(name = "bank_account_no", length = 50)
    private String bankAccountNo;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    private String notes;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}