package com.cb.th.claims.cmx.repository.fnol;


import com.cb.th.claims.cmx.entity.fnol.FnolDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FnolDetailsRepository extends JpaRepository<FnolDetail, Long> {
}
