package com.cb.th.claims.cmx.resolver.policy;

import com.cb.th.claims.cmx.entity.policy.Policy;
import com.cb.th.claims.cmx.enums.policy.PolicyStatus;
import com.cb.th.claims.cmx.repository.policy.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PolicyGraphQLController {

    private static final Logger log = LoggerFactory.getLogger(PolicyGraphQLController.class);

    private final PolicyRepository policyRepository;

    // ✅ Matches schema: getPolicyByStatus(policyStatus: String!): [Policy!]!
    @QueryMapping(name = "getPolicyByStatus")
    public List<Policy> getPolicyByStatus(@Argument("policyStatus") String policyStatus) {
        if (policyStatus == null) return List.of();

        PolicyStatus enumStatus = Arrays.stream(PolicyStatus.values())
                .filter(s -> s.name().equalsIgnoreCase(policyStatus))
                .findFirst()
                .orElse(null);

        if (enumStatus == null) {
            log.warn("Invalid policyStatus argument: '{}'", policyStatus);
            return List.of(); // return empty list instead of throwing 500
        }

        // Return all policies with the given status
        return policyRepository.findAllByPolicyStatus(enumStatus);
    }

    // Optional: enum-based list query if you also expose getPoliciesByStatus(status: PolicyStatus): [Policy!]!
    @QueryMapping(name = "getPoliciesByStatus")
    public List<Policy> getPoliciesByStatus(@Argument("status") PolicyStatus status) {
        return policyRepository.findAllByPolicyStatus(status);
    }

    @QueryMapping(name = "getPolicyByNumber")
    public Policy getPolicyByNumber(@Argument("policyNumber") String policyNumber) {
        return policyRepository.findByPolicyNumber(policyNumber).orElse(null);
    }

    @QueryMapping(name = "getAllPolicies")
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    @QueryMapping(name = "getPolicyById")
    public Policy getPolicyById(@Argument("id") Long id) {
        return policyRepository.findById(id).orElse(null);
    }
}
