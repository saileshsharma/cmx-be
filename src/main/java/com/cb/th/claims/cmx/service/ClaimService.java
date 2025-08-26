package com.cb.th.claims.cmx.service;


import com.cb.th.claims.cmx.entity.claim.Claim;

import java.util.List;

public interface ClaimService {
    Claim save(Claim entity);
    Claim findById(Long id);
    List<Claim> findAll();
    void delete(Long id);
}
