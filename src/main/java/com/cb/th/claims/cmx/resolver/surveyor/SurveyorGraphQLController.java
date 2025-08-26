package com.cb.th.claims.cmx.resolver.surveyor;

import com.cb.th.claims.cmx.entity.surveyor.Surveyor;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorJobStatus;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorStatus;
import com.cb.th.claims.cmx.repository.claim.ClaimRepository;
import com.cb.th.claims.cmx.repository.surveyor.SurveyorRepository;
import com.cb.th.claims.cmx.service.surveyor.SurveyorService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import com.cb.th.claims.cmx.dto.surveyor.UpdateSurveyorInput;


import java.util.List;

@Controller
@RequiredArgsConstructor
public class SurveyorGraphQLController {

    private final SurveyorRepository surveyorRepository;
    private final ClaimRepository claimRepo;

    private final SurveyorService service;


    // ✅ Mutation for updating a surveyor (partial update)
    @MutationMapping
    public Surveyor updateSurveyor(@Argument Long id, @Argument UpdateSurveyorInput input) {
        return service.updateSurveyor(id, input);
    }


    @QueryMapping
    public Surveyor getSurveyor(@Argument Long id) {        // <-- Long here
        return service.getSurveyor(id);
    }

    // ✅ Get all surveyors
    @QueryMapping
    public List<Surveyor> getAllSurveyors() {
        return surveyorRepository.findAll();
    }

    // ✅ Get surveyors by availability status
    @QueryMapping
    public List<Surveyor> getSurveyorsByStatus(@Argument SurveyorStatus status) {
        return surveyorRepository.findByStatus(status);
    }

    // ✅ Get surveyors by job status
    @QueryMapping
    public List<Surveyor> getSurveyorsByJobStatus(@Argument SurveyorJobStatus jobStatus) {
        return surveyorRepository.findBySurveyorJobStatus(jobStatus);
    }


}
