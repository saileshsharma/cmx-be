package com.cb.th.claims.cmx.service.impl;


import com.cb.th.claims.cmx.entity.surveyor.Surveyor;
import com.cb.th.claims.cmx.repository.surveyor.SurveyorRepository;
import com.cb.th.claims.cmx.service.SurveyorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyorServiceImpl implements SurveyorService {
    private final SurveyorRepository surveyorRepository;

    @Override
    public Surveyor save(Surveyor entity) {
        return surveyorRepository.save(entity);
    }
    @Override
    public Surveyor findById(Long id) {
        return surveyorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Surveyor not found with ID: " + id));
    }
    @Override
    public List<Surveyor> findAll() {
        return surveyorRepository.findAll();
    }
    @Override
    public void delete(Long id) {
        surveyorRepository.deleteById(id);
    }
}
