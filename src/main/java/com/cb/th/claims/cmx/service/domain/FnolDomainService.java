package com.cb.th.claims.cmx.service.domain;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FnolDomainService {

    private final ObjectMapper om;
    // private final FNOLRepository fnolRepo; // your existing repo

    @Transactional
    public void createFnolAndEmitEvent(/* your inputs */) {

        Long fnolDbId = /* saved id */ 123L;


    }

    private static String serialize(ObjectMapper om, Object o) {
        try {
            return om.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}