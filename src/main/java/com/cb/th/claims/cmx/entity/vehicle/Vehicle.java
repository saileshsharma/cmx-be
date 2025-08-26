package com.cb.th.claims.cmx.entity.vehicle;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vin; //Vehicle Identification Number

    @Column(name = "registration_number")   // 👈 important
    private String registrationNumber; //License plate number
    private String make;
    private String model;
    private Integer year;

    private String chassis;

    private String color;
    private String bodyType;

    private String engineNo; //Optional, for fraud checks
    private String fuelType; //Petrol, Diesel, Electric, Hybrid

    private String usageType; //Private, Commercial, Taxi
    private String ownerName; //Current owner's name

    private String ownerContact; //Current owner's phone/email
    private String registrationState;//State or province of registration

    private LocalDate createdAt;
    private LocalDate updatedAt;

}
