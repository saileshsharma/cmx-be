package com.cb.th.claims.cmx.dto.surveyor;



import com.cb.th.claims.cmx.enums.surveyor.SurveyorJobStatus;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateSurveyorInput {
    private String name;
    private String email;
    private String phoneNumber;

    private Double currentLat;
    private Double currentLng;

    @JsonProperty("rating_avg")   // FE sends rating_avg
    private Integer ratingAvg;

    @JsonProperty("app_version")  // FE sends app_version
    private String appVersion;

    private Integer capacityPerDay;
    private Integer activeJobsCount;

    private String skills;
    private String city;
    private String province;
    private String country;

    private Boolean internal;
    private Boolean isActive;
    private SurveyorStatus status;
    private SurveyorJobStatus surveyorJobStatus;
}