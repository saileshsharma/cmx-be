package com.cb.th.claims.cmx.service;


import com.cb.th.claims.cmx.entity.policy.Policy;

import java.util.List;

public interface PolicyService {
    Policy save(Policy entity);
    Policy findById(Long id);
    List<Policy> findAll();
    void delete(Long id);
}
