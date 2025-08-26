package com.cb.th.claims.cmx.service;


import com.cb.th.claims.cmx.entity.insured.Insured;

import java.util.List;

public interface InsuredService {
    Insured save(Insured entity);
    Insured findById(Long id);
    List<Insured> findAll();
    void delete(Long id);
}
