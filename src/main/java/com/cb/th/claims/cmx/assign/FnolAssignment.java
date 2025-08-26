package com.cb.th.claims.cmx.assign;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "claim_assignment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FnolAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long fnolId;
    private String surveyorId;
    private Double score;
    @Column(columnDefinition = "text")
    private String reason;
    private java.time.OffsetDateTime assignedAt;

    static FnolAssignment from(SurveyorAssignmentMade e) {
        return FnolAssignment.builder().fnolId(Long.valueOf(e.getFnolId())).surveyorId(e.getSurveyorId()).score(e.getScore()).reason(String.join(",", e.getReason())).assignedAt(java.time.OffsetDateTime.now()).build();
    }
}