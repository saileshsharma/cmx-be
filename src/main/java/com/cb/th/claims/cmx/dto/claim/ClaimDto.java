package com.cb.th.claims.cmx.dto.claim;

import com.cb.th.claims.cmx.dto.vehicle.VehicleDto;
import com.cb.th.claims.cmx.dto.fnol.FnolDetailsDto;
import com.cb.th.claims.cmx.dto.surveyor.SurveyorDto;
import com.cb.th.claims.cmx.enums.claim.ClaimStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClaimDto {
    private Long claimId;
    private FnolDetailsDto fnol;
    private VehicleDto vehicle;
    private LocalDateTime claimDate;
    private BigDecimal claimAmount;
    private SurveyorDto surveyor;
    private LocalDateTime createdAt;
    private ClaimStatus claimStatus;   // ✅ match entity name
}
