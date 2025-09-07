package com.cb.th.claims.cmx.repository.fnol;

import com.cb.th.claims.cmx.entity.fnol.FnolDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FnolDetailsRepository extends JpaRepository<FnolDetail, Long> {

    // Adjust table name if yours is "fnol_details"
    @Query(value = """
        SELECT *
        FROM fnol_detail f
        WHERE (f.fnol_reference_no = :ref
           OR  f.reference_no     = :ref
           OR  f.fnol_ref_no      = :ref)
        ORDER BY f.id DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<FnolDetail> findByRef(@Param("ref") String ref);
}
