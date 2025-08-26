package com.cb.th.claims.cmx.dto.vehicle;

import com.cb.th.claims.cmx.dto.insured.InsuredDto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VehicleDto {
    private Long vehicleId;
    private String registrationNumber;
    private String make;
    private String model;
    private Integer year;
    private String vin;
    private InsuredDto insured;
    private LocalDateTime createdAt;
}
