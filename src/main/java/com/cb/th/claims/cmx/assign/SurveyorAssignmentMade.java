package com.cb.th.claims.cmx.assign;


import com.cb.th.claims.cmx.events.FnolCreated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SurveyorAssignmentMade {
    private String fnolId;
    private String surveyorId;
    private double score;
    private List<String> reason;
    private Integer etaMinutes;
    private String regionId;
    private FnolCreated.Meta meta;
}