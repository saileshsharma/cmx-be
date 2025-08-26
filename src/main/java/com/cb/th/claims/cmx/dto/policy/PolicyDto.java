package com.cb.th.claims.cmx.dto.policy;

import com.cb.th.claims.cmx.dto.insured.InsuredDto;
import com.cb.th.claims.cmx.dto.vehicle.VehicleDto;
import com.cb.th.claims.cmx.enums.policy.PolicyStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PolicyDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime createdAt;
    private Long id;
    private String policyNumber;
    private String coverageType;
    private PolicyStatus policyStatus;
    private String lob;
    private String policyType; // TYPE-1, TYPE-2, TYPE-3, TYPE-4
    private Double sumInsured;
    private Double premium;
    private InsuredDto insured;
    private VehicleDto vehicleDto;
}
