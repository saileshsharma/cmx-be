package com.cb.th.claims.cmx.service.policy;

import com.cb.th.claims.cmx.dto.policy.PolicyLookupResponse;
import com.cb.th.claims.cmx.entity.policy.Policy;
import com.cb.th.claims.cmx.repository.policy.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class PolicyLookupService {

    private final PolicyRepository policyRepo;

    public PolicyLookupService(PolicyRepository policyRepo) {
        this.policyRepo = policyRepo;
    }

    private static String normalize(String plate) {
        return plate == null ? null : plate.toLowerCase().replaceAll("[\\s-]", "");
    }

    @Transactional(readOnly = true)
    public List<PolicyLookupResponse> findAllByPlate(String rawPlate, boolean activeOnly) {
        var plate = normalize(rawPlate);
        var policies = activeOnly ? policyRepo.findActiveByLicensePlateOrderByEndDesc(plate) : policyRepo.findByLicensePlate(plate);

        return policies.stream().sorted(Comparator.comparing(Policy::getEndDate, Comparator.nullsLast(Comparator.reverseOrder()))).map(p -> new PolicyLookupResponse(p.getPolicyNumber(), p.getPolicyStatus().name(), p.getCoverageType(), p.getStartDate(), p.getEndDate(), p.getInsured() != null ? p.getInsured().getFirstName() : null, p.getVehicle() != null ? p.getVehicle().getRegistrationNumber() : null, p.getVehicle() != null ? p.getVehicle().getId() : null, p.getId())).toList();
    }

    @Transactional(readOnly = true)
    public PolicyLookupResponse findBestMatchActive(String rawPlate) {
        return findAllByPlate(rawPlate, true).stream().findFirst().orElse(null);
    }
}