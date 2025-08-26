package com.cb.th.claims.cmx.repository.claim;


import com.cb.th.claims.cmx.entity.claim.Claim;
import com.cb.th.claims.cmx.enums.claim.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByClaimStatus(ClaimStatus claimStatus);

    // ---- Pagination (built-in & by status) ----
    Page<Claim> findAll(Pageable pageable);

    Page<Claim> findByClaimStatus(ClaimStatus claimStatus, Pageable pageable);

}
