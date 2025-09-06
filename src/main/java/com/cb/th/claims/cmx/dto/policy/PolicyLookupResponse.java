package com.cb.th.claims.cmx.dto.policy;


import java.time.LocalDate;

public record PolicyLookupResponse(String policyNumber, String policyStatus, String coverageType, LocalDate startDate,
                                   LocalDate endDate, String insuredName, String registrationNumber, Long vehicleId,
                                   Long policyId) {
}


