package com.cb.th.claims.cmx.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FnolCreated {
    private String fnolId;
    //private String fnolReferenceNo;
    private String policyNumber;
    private String registrationNumber;
    private Accident accident;
    private String regionId;
    private List<String> requiredSkills;
    private Meta meta;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Accident {
        double lat;
        double lng;
        Instant time;
        String severity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        String traceId;
    }
}
