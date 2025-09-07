package com.cb.th.claims.cmx.repository.surveyor.projection;


public interface SurveyorNearestRow {
    Long getId();

    String getName();

    String getEmail();

    String getPhoneNumber();

    String getStatus();

    String getSurveyorJobStatus();

    Integer getActiveJobsCount();

    Integer getCapacityPerDay();

    Double getDistanceKm();

    String getSkills();
}