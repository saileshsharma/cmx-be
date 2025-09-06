package com.cb.th.claims.cmx.repository.policy;

import com.cb.th.claims.cmx.entity.policy.Policy;
import com.cb.th.claims.cmx.enums.policy.PolicyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Optional<Policy> findByPolicyNumber(String policyNumber);

    // Return ALL policies by status
    List<Policy> findAllByPolicyStatus(PolicyStatus policyStatus);

    // Alias (keep if used elsewhere)
    List<Policy> findByPolicyStatus(PolicyStatus policyStatus);

    // Pagination helpers
    Page<Policy> findAll(Pageable pageable);

    Page<Policy> findAllByPolicyStatus(PolicyStatus policyStatus, Pageable pageable);

    // Optional single-most-recent helper (kept for other callers)
    Optional<Policy> findFirstByPolicyStatusOrderByIdDesc(PolicyStatus status);


    @Query("""
              select p from Policy p
              join fetch p.vehicle v
              where regexp_replace(lower(v.registrationNumber), '\\s|-', '', 'g')
                    = regexp_replace(lower(:rawPlate), '\\s|-', '', 'g')
            """)
    List<Policy> findByLicensePlate(String rawPlate);

    @Query("""
              select p from Policy p
              join fetch p.vehicle v
              where regexp_replace(lower(v.registrationNumber), '\\s|-', '', 'g')
                    = regexp_replace(lower(:rawPlate), '\\s|-', '', 'g')
                and p.policyStatus in ('ACTIVE','IN_FORCE')
              order by p.endDate desc nulls last
            """)
    List<Policy> findActiveByLicensePlateOrderByEndDesc(String rawPlate);

}
