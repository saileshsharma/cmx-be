package com.cb.th.claims.cmx.service;


import com.cb.th.claims.cmx.entity.surveyor.Surveyor;

import java.util.List;

public interface SurveyorService {
    Surveyor save(Surveyor entity);
    Surveyor findById(Long id);
    List<Surveyor> findAll();
    void delete(Long id);
}
