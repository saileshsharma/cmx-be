package com.cb.th.claims.cmx.service.surveyor;

import com.cb.th.claims.cmx.service.surveyor.
import com.cb.th.claims.cmx.repo.SurveyorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class SurveyorLocatorService {
    private final SurveyorRepository repo;
    public List<SurveyorNearestRow> nearestByLatLng(double lat, double lng, int limit) {
        return repo.findNearestAvailableNative(lat, lng, PageRequest.of(0, Math.max(1, limit)));
    }
}




