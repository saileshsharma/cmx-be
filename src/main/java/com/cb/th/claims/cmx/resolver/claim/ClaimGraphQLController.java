package com.cb.th.claims.cmx.resolver.claim;

import com.cb.th.claims.cmx.entity.claim.Claim;
import com.cb.th.claims.cmx.enums.claim.ClaimStatus;
import com.cb.th.claims.cmx.repository.claim.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ClaimGraphQLController {

    private final ClaimRepository claimRepository;

    @QueryMapping
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    // ✅ Get claim by ID
    @QueryMapping
    public Claim getClaimById(@Argument Long id) {
        return claimRepository.findById(id).orElse(null);
    }

    // ✅ Get claims by status
    @QueryMapping
    public List<Claim> getClaimsByStatus(@Argument ClaimStatus status) {
        return claimRepository.findByClaimStatus(status);
    }


    @MutationMapping
    public Claim updateClaimStatus(@Argument Long id, @Argument ClaimStatus status) {
        Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("Claim not found with ID: " + id));

        claim.setClaimStatus(status);
        claimRepository.save(claim);  // ✅ Save the updated claim


        return claim;  // ✅ Return updated claim
    }


    @MutationMapping
    public List<Claim> updateAllClaimsStatus(@Argument ClaimStatus status) {
        List<Claim> claims = claimRepository.findAll();
        claims.forEach(claim -> claim.setClaimStatus(status));
        claimRepository.saveAll(claims);
        return claims;  // ✅ Return updated claims
    }

}
