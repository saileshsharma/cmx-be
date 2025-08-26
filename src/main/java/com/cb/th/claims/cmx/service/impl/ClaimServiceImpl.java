package com.cb.th.claims.cmx.service.impl;


import com.cb.th.claims.cmx.entity.claim.Claim;
import com.cb.th.claims.cmx.repository.claim.ClaimRepository;
import com.cb.th.claims.cmx.service.ClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {
    private final ClaimRepository claimRepository;

    @Override
    public Claim save(Claim entity) {
        return claimRepository.save(entity);
    }
    @Override
    public Claim findById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found with ID: " + id));
    }
    @Override
    public List<Claim> findAll() {
        return claimRepository.findAll();
    }
    @Override
    public void delete(Long id) {
        claimRepository.deleteById(id);
    }
}
