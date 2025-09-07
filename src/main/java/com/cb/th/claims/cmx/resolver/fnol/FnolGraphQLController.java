package com.cb.th.claims.cmx.resolver.fnol;

import com.cb.th.claims.cmx.entity.fnol.FNOL;
import com.cb.th.claims.cmx.entity.fnol.FnolDetail;
import com.cb.th.claims.cmx.enums.claim.ClaimSeverity;
import com.cb.th.claims.cmx.enums.fnol.FNOLState;
import com.cb.th.claims.cmx.repository.fnol.FNOLRepository;
import com.cb.th.claims.cmx.repository.policy.PolicyRepository;
import com.cb.th.claims.cmx.service.fnol.FnolService;
import com.cb.th.claims.cmx.service.fnol.FnolService.CreateFnolCommand;
import com.cb.th.claims.cmx.service.fnol.FnolService.CreateFnolPayload;
import com.cb.th.claims.cmx.service.surveyor.SurveyorAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FnolGraphQLController {

    private final FNOLRepository fnolRepository; // simple reads
    private final FnolService fnolService;       // business logic

    private final PolicyRepository policyRepository; // simple reads
    private final SurveyorAssignmentService assignmentService;


    // ===== Queries =====

    @QueryMapping
    @Transactional(readOnly = true)
    public List<FNOL> getAllFnol() {
        return fnolRepository.findAll();
    }

    @QueryMapping
    @Transactional(readOnly = true)
    public FNOL getFnolById(@Argument("id") Long id) {
        return fnolRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("FNOL not found with ID: " + id));
    }

    // ===== Mutations =====

    /**
     * Schema: createFnol(...): CreateFnolPayload
     * The payload must expose a 'getFnol()' getter so GraphQL field 'fnol' resolves.
     * <p>
     * NOTE: If your Address IDs are UUID/String, change accidentLocationId to String
     * here and in CreateFnolCommand + repository generic.
     */
    @MutationMapping
    public CreateFnolPayload createFnol(@Argument("policyNumber") String policyNumber, @Argument("registrationNumber") String registrationNumber, @Argument("accidentLocationId") Long accidentLocationId, // change to String if your Address ID is UUID
                                        @Argument("description") String description, @Argument("severity") ClaimSeverity severity, @Argument("accidentDate") String accidentDate) {

        if (policyNumber == null || policyNumber.isBlank()) {
            throw new IllegalArgumentException("policyNumber is required");
        }
        if (registrationNumber == null || registrationNumber.isBlank()) {
            throw new IllegalArgumentException("registrationNumber is required");
        }
        if (accidentLocationId == null) {
            throw new IllegalArgumentException("accidentLocationId is required");
        }
        if (severity == null) {
            throw new IllegalArgumentException("severity is required");
        }
        if (accidentDate == null || accidentDate.isBlank()) {
            throw new IllegalArgumentException("accidentDate is required");
        }

        return fnolService.createFnol(new CreateFnolCommand(policyNumber, registrationNumber, accidentLocationId, description, severity, accidentDate));
    }

    /**
     * Schema: updateFnol(...): FnolDetails
     * Returning FNOL entity is fine if its getters match GraphQL fields.
     */
    @MutationMapping
    @Transactional
    public FNOL updateFnol(@Argument("id") Long id, @Argument("fnolState") FNOLState fnolState,   // nullable
                           @Argument("severity") ClaimSeverity severity, // nullable
                           @Argument("description") String description   // nullable
    ) {
        FNOL fnol = fnolRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("FNOL not found with ID: " + id));

        if (fnolState != null) fnol.setFnolState(fnolState);
        if (severity != null) fnol.setSeverity(severity);
        if (description != null && !description.isBlank()) fnol.setDescription(description);

        return fnolRepository.save(fnol);
    }




/*    @QueryMapping
    @Transactional(readOnly = true)
    public FNOL latestFnol(@Argument String policyNumber, @Argument String registrationNumber) {
        var policy = policyRepository.findByPolicyNumber(policyNumber).orElseThrow(() -> new BusinessException("POLICY_NOT_FOUND", "Policy not found: " + policyNumber, 404));
        var reg = FnolService.normalizeRegistration(registrationNumber);
        var last = fnolRepository.findLatestByPolicyAndReg(policy.getId(), reg);
        return last.orElse(null);
    }*/



/*
    @QueryMapping
    @Transactional(readOnly = true)
    public FNOL latestFnol(@Argument String policyNumber,
                           @Argument String registrationNumber) {
        var policy = policyRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new BusinessException("POLICY_NOT_FOUND","Policy not found: "+policyNumber,404));
        var reg = FnolService.normalizeRegistration(registrationNumber);
        var last = fnolRepository.findLatestByPolicyAndReg(policy.getId(), reg);
        return last.orElse(null);
    }*/

}
