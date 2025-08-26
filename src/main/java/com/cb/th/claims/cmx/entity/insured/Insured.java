package com.cb.th.claims.cmx.entity.insured;

import com.cb.th.claims.cmx.entity.address.Address;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "insured")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insured {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String firstName; //VARCHAR(100) NOT NULL,
    private String lastName;           //VARCHAR(100) NOT NULL,
    private LocalDate dob;       //DATE NOT NULL,
    private String gender;              //VARCHAR(10) CHECK (gender IN ('Male', 'Female', 'Other')),
    private String nationalId;         //VARCHAR(50), -- could be NRIC, SSN, etc.
    private String passportNumber; //VARCHAR(50),

    private String email;
    private String phoneNumber;

    private String addressLine1;      //VARCHAR(255) NOT NULL,
    private String addressLine2;     // VARCHAR(255),
    private String city;       //VARCHAR(100) NOT NULL,
    private String province;     //VARCHAR(100),
    private String postalCode;       // VARCHAR(20),
    private String country;       //VARCHAR(100) DEFAULT 'Thailand', -- adjust default if needed

    private String driverLicenseNo;   //VARCHAR(50) NOT NULL,
    private LocalDate licenseIssueDate;  //DATE,
    private LocalDate licenseExpiryDate; //DATE,

    //Risk & underwriting info
    private String occupation;         //VARCHAR(100),
    private String maritalStatus;      //VARCHAR(20) CHECK (marital_status IN ('Single', 'Married', 'Divorced', 'Widowed')),
    private int yearsDriving;       //SMALLINT CHECK (years_driving >= 0),

    //-- Audit fields
    private LocalDate createdAt;          //TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    private LocalDate updatedAt;          //TIMESTAMP DEFAULT CURRENT_TIMESTAMP


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;
}