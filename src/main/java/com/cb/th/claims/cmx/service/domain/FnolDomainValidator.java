package com.cb.th.claims.cmx.service.domain;


import com.cb.th.claims.cmx.entity.policy.Policy;
import com.cb.th.claims.cmx.entity.vehicle.Vehicle;
import com.cb.th.claims.cmx.enums.policy.PolicyStatus;
import com.cb.th.claims.cmx.exception.BusinessException;
import com.cb.th.claims.cmx.repository.fnol.FNOLRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class FnolDomainValidator {

    private final FNOLRepository fnolRepository;

    private static final int DUPLICATE_WINDOW_HOURS = 24;

    public void validatePolicyEligibilityOrThrow(Policy policy, LocalDate accidentDate) {
        final PolicyStatus status = coercePolicyStatusOrThrow(policy.getPolicyStatus());

        final boolean eligible = (status == PolicyStatus.BIND) || (status == PolicyStatus.IN_FORCE);
        if (!eligible) {
            throw new BusinessException("POLICY_NOT_ELIGIBLE", "Policy must be BIND or IN_FORCE to create FNOL. Current status: " + status.name(), 400);
        }

        final LocalDate start = policy.getStartDate();
        final LocalDate end = policy.getEndDate();

        if (start != null && accidentDate.isBefore(start)) {
            throw new BusinessException("ACCIDENT_BEFORE_POLICY", "Accident date " + accidentDate + " is before policy start date " + start + ".", 400);
        }
        if (end != null && accidentDate.isAfter(end)) {
            throw new BusinessException("ACCIDENT_AFTER_POLICY", "Accident date " + accidentDate + " is after policy end date " + end + ".", 400);
        }
    }

    public void validateVehicleBelongsToPolicyOrThrow(Vehicle vehicle, Policy policy) {
        if (policy == null || policy.getId() == null) {
            throw new BusinessException("POLICY_INVALID", "Policy is missing an ID.", 400);
        }
        if (policy.getVehicle() == null || policy.getVehicle().getId() == null) {
            throw new BusinessException("POLICY_NO_VEHICLE", "Policy has no linked vehicle.", 400);
        }
        if (!policy.getVehicle().getId().equals(vehicle.getId())) {
            throw new BusinessException("VEHICLE_POLICY_MISMATCH", "Vehicle registration does not belong to the specified policy.", 400);
        }
    }

    public void validateNoDuplicateOrOpenFnolOrThrow(Policy policy, String normalizedReg, LocalDateTime accidentTs) {
        final LocalDateTime from = accidentTs.minusHours(DUPLICATE_WINDOW_HOURS);
        final LocalDateTime to = accidentTs.plusHours(DUPLICATE_WINDOW_HOURS);

        final boolean duplicateInWindow = fnolRepository.existsDuplicateForPolicyAndRegWithin(policy.getId(), normalizedReg, from, to);

        if (duplicateInWindow) {
            throw new BusinessException("FNOL_DUPLICATE_WINDOW", "An FNOL already exists for this policy & registration around the given accident time.", 409);
        }

        // If you later add "open" check, re-enable here:
        // boolean hasOpen = fnolRepository.existsOpenForPolicyAndReg(policy.getId(), normalizedReg, FNOLState.REJECTED);
        // if (hasOpen) throw new BusinessException("FNOL_ALREADY_OPEN", "An active FNOL already exists.", 409);
    }

    private static PolicyStatus coercePolicyStatusOrThrow(Object rawStatus) {
        if (rawStatus == null) {
            throw new BusinessException("POLICY_STATUS_MISSING", "Policy status is missing.", 400);
        }
        if (rawStatus instanceof PolicyStatus ps) {
            return ps;
        }
        final String statusText = String.valueOf(rawStatus).trim().toUpperCase(Locale.ROOT);
        try {
            return PolicyStatus.valueOf(statusText);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("POLICY_STATUS_INVALID", "Unknown policy status: " + statusText, 400);
        }
    }
}