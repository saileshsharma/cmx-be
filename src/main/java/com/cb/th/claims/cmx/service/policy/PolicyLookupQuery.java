package com.cb.th.claims.cmx.service.policy;


import com.cb.th.claims.cmx.service.policy.PolicyLookupService;
import com.cb.th.claims.cmx.dto.policy.PolicyLookupResponse;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PolicyLookupQuery {

    private final PolicyLookupService svc;

    public PolicyLookupQuery(PolicyLookupService svc) {
        this.svc = svc;
    }

    @QueryMapping
    public PolicyLookupResponse policyByLicensePlate(@Argument String plate) {
        return svc.findBestMatchActive(plate);
    }

    @QueryMapping
    public List<PolicyLookupResponse> policiesByLicensePlate(@Argument String plate, @Argument Boolean includeInactive) {
        boolean activeOnly = includeInactive == null || !includeInactive;
        return svc.findAllByPlate(plate, !includeInactive && activeOnly);
    }
}