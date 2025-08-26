package com.cb.th.claims.cmx.service.policy;


import com.cb.th.claims.cmx.repository.policy.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository repo;
/*

    @Cacheable(cacheNames = "policyByNumber", key = "#policyByNumberCacheKey", unless = "#result == null")
    @Transactional(readOnly = true)
    public Policy getByPolicyNumber(String policyNumber) {
        return repo.findByPolicyNumber(policyNumber).orElse(null);
    }

    @CacheEvict(cacheNames = {"policyByNumber", "fnolListByPolicy"}, key = "#policy.policyNumber", beforeInvocation = false)
    @Transactional
    public Policy updatePolicy(Policy policy) {
        return repo.save(policy);
    }
*/

}