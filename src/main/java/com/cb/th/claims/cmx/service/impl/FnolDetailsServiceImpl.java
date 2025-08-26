package com.cb.th.claims.cmx.service.impl;


import com.cb.th.claims.cmx.entity.fnol.FnolDetail;
import com.cb.th.claims.cmx.repository.fnol.FnolDetailsRepository;
import com.cb.th.claims.cmx.service.FnolDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FnolDetailsServiceImpl implements FnolDetailsService {
    private final FnolDetailsRepository fnolDetailsRepository;

    @Override
    public FnolDetail save(FnolDetail entity) {
        return fnolDetailsRepository.save(entity);
    }
    @Override
    public FnolDetail findById(Long id) {
        return fnolDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FnolDetails not found with ID: " + id));
    }
    @Override
    public List<FnolDetail> findAll() {
        return fnolDetailsRepository.findAll();
    }
    @Override
    public void delete(Long id) {
        fnolDetailsRepository.deleteById(id);
    }
}
