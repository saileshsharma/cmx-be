package com.cb.th.claims.cmx.repository.fnol.assignment;


import com.cb.th.claims.cmx.assign.FnolAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface FnolAssignmentRepository extends JpaRepository<FnolAssignment, Long> {
    Optional<FnolAssignment> findByFnolId(Long fnolId);

    boolean existsByFnolId(Long fnolId);
}