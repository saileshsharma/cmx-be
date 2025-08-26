package com.cb.th.claims.cmx.service.impl;


import com.cb.th.claims.cmx.entity.policy.Policy;
import com.cb.th.claims.cmx.repository.policy.PolicyRepository;
import com.cb.th.claims.cmx.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {
    private final PolicyRepository policyRepository;

    @Override
    public Policy save(Policy entity) {
        return policyRepository.save(entity);
    }
    @Override
    public Policy findById(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with ID: " + id));
    }
    @Override
    public List<Policy> findAll() {
        return policyRepository.findAll();
    }
    @Override
    public void delete(Long id) {
        policyRepository.deleteById(id);
    }
}
