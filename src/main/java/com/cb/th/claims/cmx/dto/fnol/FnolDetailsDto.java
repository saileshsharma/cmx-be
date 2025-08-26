package com.cb.th.claims.cmx.dto.fnol;

import com.cb.th.claims.cmx.dto.address.AddressDto;
import com.cb.th.claims.cmx.dto.insured.InsuredDto;
import com.cb.th.claims.cmx.dto.policy.PolicyDto;
import com.cb.th.claims.cmx.dto.surveyor.SurveyorDto;
import com.cb.th.claims.cmx.dto.vehicle.VehicleDto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FnolDetailsDto {
    private Long fnolId;
    private PolicyDto policy;
    private VehicleDto vehicle;
    private InsuredDto insured;
    private AddressDto accidentLocation;
    private LocalDateTime accidentDate;
    private String description;
    private String policeReportNo;
    private String severity;
    private SurveyorDto surveyor;
    private LocalDateTime createdAt;
}
