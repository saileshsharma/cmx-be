package com.cb.th.claims.cmx.repository.insured;


import com.cb.th.claims.cmx.entity.insured.Insured;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuredRepository extends JpaRepository<Insured, Long> {
}
