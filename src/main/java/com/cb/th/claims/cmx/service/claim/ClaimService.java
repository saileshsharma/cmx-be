package com.cb.th.claims.cmx.service.claim;

import com.cb.th.claims.cmx.entity.claim.Claim;
import com.cb.th.claims.cmx.entity.fnol.FNOL;
import com.cb.th.claims.cmx.enums.claim.ClaimStatus;
import com.cb.th.claims.cmx.repository.claim.ClaimRepository;
import org.springframework.stereotype.Service;

@Service
public class ClaimService {
    private final ClaimRepository claimRepository;


    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;

    }

    public Claim createInitialForFnol(FNOL fnol, ClaimStatus initialStatus) {
        Claim c = new Claim();
        c.setFnol(fnol);
        c.setClaimStatus(initialStatus);
        //c.setClaimNumber(claimNumberService.next());
        return claimRepository.save(c);
    }
}