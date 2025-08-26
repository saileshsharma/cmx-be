package com.cb.th.claims.cmx.service.impl;


import com.cb.th.claims.cmx.entity.insured.Insured;
import com.cb.th.claims.cmx.repository.insured.InsuredRepository;
import com.cb.th.claims.cmx.service.InsuredService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InsuredServiceImpl implements InsuredService {
    private final InsuredRepository insuredRepository;

    @Override
    public Insured save(Insured entity) {
        return insuredRepository.save(entity);
    }
    @Override
    public Insured findById(Long id) {
        return insuredRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insured not found with ID: " + id));
    }
    @Override
    public List<Insured> findAll() {
        return insuredRepository.findAll();
    }
    @Override
    public void delete(Long id) {
        insuredRepository.deleteById(id);
    }
}
