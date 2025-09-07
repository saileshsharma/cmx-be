package com.cb.th.claims.cmx.dto.surveyor;


import com.cb.th.claims.cmx.payload.AssignSurveyorPayload;

public interface SurveyorAssignmentService {
    AssignSurveyorPayload assign(String fnolReferenceNo);
}