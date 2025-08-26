package com.cb.th.claims.cmx.repository.fnol;

import com.cb.th.claims.cmx.entity.fnol.FNOL;
import com.cb.th.claims.cmx.enums.fnol.FNOLState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FNOLRepository extends JpaRepository<FNOL, Long> {
    List<FNOL> findBySeverity(String severity);

    Optional<FNOL> findByAccidentLocation_Id(Long addressId);

    boolean existsByAccidentLocation_Id(Long accidentLocationId);


    // ✅ Use explicit JPQL with DB-side normalization of the plate
    @Query("""
                select (count(f) > 0) from FNOL f
                join f.vehicle v
                where f.policy.id = :policyId
                  and upper(replace(replace(v.registrationNumber, '-', ''), ' ', '')) = :normalizedReg
                  and f.accidentDate between :from and :to
            """)
    boolean existsDuplicateForPolicyAndRegWithin(Long policyId, String normalizedReg, LocalDateTime from, LocalDateTime to);

    @Query("""
                select (count(f) > 0) from FNOL f
                join f.vehicle v
                where f.policy.id = :policyId
                  and upper(replace(replace(v.registrationNumber, '-', ''), ' ', '')) = :normalizedReg
                  and f.fnolState <> :excludedState
            """)
    boolean existsOpenForPolicyAndReg(Long policyId, String normalizedReg, FNOLState excludedState);

}

