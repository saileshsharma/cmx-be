package com.cb.th.claims.cmx.resolver.surveyor;

import com.cb.th.claims.cmx.dto.surveyor.UpdateSurveyorInput;
import com.cb.th.claims.cmx.entity.surveyor.Surveyor;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorJobStatus;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorStatus;
import com.cb.th.claims.cmx.payload.AssignSurveyorPayload;
import com.cb.th.claims.cmx.repository.surveyor.SurveyorRepository;
import com.cb.th.claims.cmx.service.surveyor.SurveyorAssignmentService;
import com.cb.th.claims.cmx.service.surveyor.SurveyorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SurveyorGraphQLController {

    private final SurveyorRepository surveyorRepository;
    private final SurveyorService service;
    private final SurveyorAssignmentService assignmentService;

    /* =========================
       Queries
       ========================= */

    @QueryMapping
    public Surveyor getSurveyor(@Argument Long id) {
        return service.getSurveyor(id);
    }

    @QueryMapping
    public List<Surveyor> getAllSurveyors() {
        return surveyorRepository.findAll();
    }

    @QueryMapping
    public List<Surveyor> getSurveyorsByStatus(@Argument SurveyorStatus status) {
        return surveyorRepository.findByStatus(status);
    }

    @QueryMapping
    public List<Surveyor> getSurveyorsByJobStatus(@Argument SurveyorJobStatus jobStatus) {
        return surveyorRepository.findBySurveyorJobStatus(jobStatus);
    }

    /* =========================
       Mutations
       ========================= */

    // Partial update
    @MutationMapping
    public Surveyor updateSurveyor(@Argument Long id, @Argument UpdateSurveyorInput input) {
        return service.updateSurveyor(id, input);
    }

    // Manual assignment by surveyorId
    @MutationMapping
    public AssignSurveyorPayload assignSurveyor(@Argument Long fnolId, @Argument Long surveyorId) {
        assignmentService.assignSurveyor(fnolId, surveyorId);

        Surveyor s = surveyorRepository.findById(surveyorId).orElseThrow(() -> new IllegalArgumentException("Surveyor not found after assign: " + surveyorId));

        String msg = "Assigned surveyor %s (id=%d) to FNOL id=%d".formatted(s.getName(), s.getId(), fnolId);

        return AssignSurveyorPayload.builder().id(String.valueOf(fnolId))               // if you prefer FNOL ref, set it here instead
                .fnolReferenceNo(null)                    // populate if you have it at this layer
                .status("SUCCESS").message(msg).fnolId(fnolId).surveyorId(s.getId()).surveyorName(s.getName()).assignedSurveyor(toView(s)).build();
    }

    // Auto-assign: nearest AVAILABLE with capacity
    @MutationMapping
    public AssignSurveyorPayload autoAssignSurveyor(@Argument Long fnolId, @Argument Integer take) {
        int limit = (take == null || take <= 0) ? 10 : take;
        Surveyor s = assignmentService.autoAssignNearestSurveyor(fnolId, limit);

        String msg = "Auto-assigned surveyor %s (id=%d) to FNOL id=%d".formatted(s.getName(), s.getId(), fnolId);

        return AssignSurveyorPayload.builder().id(String.valueOf(fnolId)).fnolReferenceNo(null)                    // populate if available
                .status("SUCCESS").message(msg).fnolId(fnolId).surveyorId(s.getId()).surveyorName(s.getName()).assignedSurveyor(toView(s)).build();
    }

    /* =========================
       Helpers
       ========================= */

    private AssignSurveyorPayload.SurveyorView toView(Surveyor s) {
        return AssignSurveyorPayload.SurveyorView.builder().id(s.getId() != null ? s.getId().toString() : null).name(s.getName()).email(s.getEmail()).phone(s.getPhoneNumber()).status(s.getStatus() != null ? s.getStatus().name() : null).jobStatus(s.getSurveyorJobStatus() != null ? s.getSurveyorJobStatus().name() : null).city(s.getCity()).province(s.getProvince()).country(s.getCountry()).build();
    }
}
