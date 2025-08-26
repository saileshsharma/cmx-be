package com.cb.th.claims.cmx.dto.surveyor;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SurveyorDto {
    private Long surveyorId;
    private String fullName;
    private String licenseNo;
    private String contactNumber;
    private String email;
    private String location;
    private String status;
    private LocalDateTime createdAt;

    private Double currentLat;
    private Double currentLng;
}
