package com.cb.th.claims.cmx.resolver.claim;

import com.cb.th.claims.cmx.entity.claim.Claim;
import com.cb.th.claims.cmx.enums.claim.ClaimSeverity;
import com.cb.th.claims.cmx.enums.claim.ClaimStatus;
import com.cb.th.claims.cmx.repository.claim.ClaimRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ClaimGraphQLController {

    private final ClaimRepository claimRepository;

    // -------- Queries --------a
    @QueryMapping
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    @QueryMapping
    public Claim getClaimById(@Argument("id") Long id) {
        return claimRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Claim> getClaimsByStatus(@Argument("status") ClaimStatus status) {
        return claimRepository.findByClaimStatus(status);
    }

    // -------- Mutations --------

    @MutationMapping(name = "updateClaimStatus")
    public Claim updateClaimStatus(@Argument("id") Long id, @Argument("status") ClaimStatus status) {
        Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("Claim not found with ID: " + id));
        claim.setClaimStatus(status);
        Claim saved = claimRepository.save(claim);
        log.info("updateClaimStatus OK: id={}, newStatus={}", id, status);
        return saved;
    }

    @MutationMapping(name = "updateAllClaimsStatus")
    public List<Claim> updateAllClaimsStatus(@Argument("status") ClaimStatus status) {
        List<Claim> claims = claimRepository.findAll();
        claims.forEach(c -> c.setClaimStatus(status));
        List<Claim> saved = claimRepository.saveAll(claims);
        log.info("updateAllClaimsStatus OK: count={}, newStatus={}", saved.size(), status);
        return saved;
    }

    @MutationMapping(name = "updateClaim")
    public Claim updateClaim(@Argument("id") Long id, @Argument("input") UpdateClaimInput input) {
        Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("Claim not found with ID: " + id));

        // Apply only provided fields
        if (input.getClaimStatus() != null) claim.setClaimStatus(input.getClaimStatus());
        if (input.getClaimAmount() != null) claim.setClaimAmount(input.getClaimAmount());
        if (input.getIncidentDate() != null) claim.setIncidentDate(input.getIncidentDate());
        if (input.getClaimDate() != null) claim.setClaimDate(input.getClaimDate());
        if (input.getDateReported() != null) claim.setDateReported(input.getDateReported());
        if (input.getClaimSeverity() != null) claim.setClaimSeverity(input.getClaimSeverity());
        if (input.getLocation() != null) claim.setLocation(input.getLocation());

        Claim saved = claimRepository.save(claim);
        log.info("updateClaim OK: id={}, input={}", id, input);
        return saved;
    }

    @Data
    public static class UpdateClaimInput {
        private ClaimStatus claimStatus;   // maps to entity.claimStatus
        private Double claimAmount;
        private LocalDate incidentDate;
        private LocalDate claimDate;
        private LocalDate dateReported;
        private ClaimSeverity claimSeverity;
        private String location;
    }


    // === NEW: Duplicate mutation ===
    private final Random rnd = new Random();


    @MutationMapping(name = "duplicateClaim")
    @Transactional
    public Claim duplicateClaim(@Argument("id") Long id) {
        Claim original = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("Claim not found with ID: " + id));

        Claim copy = new Claim();
        copy.setId(null); // important with SERIAL/IDENTITY

        // EITHER: rely on DB default by not setting claimNumber
        // copy.setClaimNumber(null);

        // OR: explicitly fetch from DB generator
        copy.setClaimNumber(claimRepository.nextClaimNumber());

        copy.setClaimAmount(original.getClaimAmount());
        copy.setClaimSeverity(original.getClaimSeverity());
        copy.setIncidentDate(original.getIncidentDate());
        copy.setClaimDate(original.getClaimDate());
        copy.setDateReported(original.getDateReported());
        copy.setLocation(original.getLocation());
        copy.setClaimStatus(ClaimStatus.REGISTERED);

        return claimRepository.save(copy);
    }


    @QueryMapping
    public List<Claim> getClaimsByPolicyNumber(@Argument String policyNumber) {
        return claimRepository.findByPolicyPolicyNumber(policyNumber);
    }

}
