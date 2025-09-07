package com.cb.th.claims.cmx.subscriptions.dto;

import com.cb.th.claims.cmx.enums.surveyor.SurveyorJobStatus;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FnolAssignmentNoticeDto {
    private String fnolReferenceNo;
    private String status;     // e.g. "ASSIGNED"
    private String message;
    private String timestamp;  // ISO-8601
    private SurveyorDto surveyor;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SurveyorDto {
        private String id;
        private String name;
        private String email;
        private String phoneNumber;
        private SurveyorStatus status;        // SurveyorStatus as String
        private String SurveyorJobStatus;     // SurveyorJobStatus as String
        private SurveyorJobStatus activeJobsCount;
        private Integer capacityPerDay;
        private Double currentLat;
        private Double currentLng;
    }
}
