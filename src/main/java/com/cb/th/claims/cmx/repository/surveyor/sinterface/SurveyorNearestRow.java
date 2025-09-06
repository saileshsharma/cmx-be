package com.cb.th.claims.cmx.repository.surveyor.sinterface;


public interface SurveyorNearestRow {
    Long getId();
    String getName();
    String getEmail();
    String getPhoneNumber();
    String getStatus();            // enum name as text
    String getSurveyorJobStatus(); // enum name as text
    Integer getActiveJobsCount();
    String getCapacityPerDay();    // you store it as text
    Double getDistanceKm();
}


