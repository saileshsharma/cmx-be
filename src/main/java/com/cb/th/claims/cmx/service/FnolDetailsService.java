package com.cb.th.claims.cmx.service;


import com.cb.th.claims.cmx.entity.fnol.FnolDetail;

import java.util.List;

public interface FnolDetailsService {
    FnolDetail save(FnolDetail entity);
    FnolDetail findById(Long id);
    List<FnolDetail> findAll();
    void delete(Long id);
}
