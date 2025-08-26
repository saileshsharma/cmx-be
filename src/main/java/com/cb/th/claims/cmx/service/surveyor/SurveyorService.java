// AddressService.java
package com.cb.th.claims.cmx.service.surveyor;

import com.cb.th.claims.cmx.dto.surveyor.UpdateSurveyorInput;
import com.cb.th.claims.cmx.entity.surveyor.Surveyor;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorStatus;
import com.cb.th.claims.cmx.repository.surveyor.SurveyorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SurveyorService {

    private final SurveyorRepository surveyorRepository;

    public Surveyor getSurveyor(Long id) {
        return surveyorRepository.findById(id).orElse(null);
    }


    @Transactional
    public Surveyor updateSurveyorStatus(Long id, SurveyorStatus status) {
        Surveyor s = surveyorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Surveyor not found: " + id));

        s.setStatus(status);

        // keep isActive consistent: only INACTIVE => false
        if (s.getIsActive() == null || status == SurveyorStatus.INACTIVE || status == SurveyorStatus.AVAILABLE) {
            s.setIsActive(status != SurveyorStatus.INACTIVE);
        }

        s.setUpdatedAt(java.time.LocalDate.now());
        return surveyorRepository.saveAndFlush(s);
    }


    @Transactional
    public Surveyor updateSurveyor(Long id, UpdateSurveyorInput in) {
        Surveyor s = surveyorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Surveyor not found: " + id));

        // Strings
        if (in.getName() != null) s.setName(in.getName());
        if (in.getEmail() != null) s.setEmail(in.getEmail());
        if (in.getPhoneNumber() != null) s.setPhoneNumber(in.getPhoneNumber());
        if (in.getSkills() != null) s.setSkills(in.getSkills());
        if (in.getCity() != null) s.setCity(in.getCity());
        if (in.getProvince() != null) s.setProvince(in.getProvince());
        if (in.getCountry() != null) s.setCountry(in.getCountry());
        if (in.getAppVersion() != null) s.setAppVersion(in.getAppVersion());
        if (in.getInternal() !=null) s.setInternal(in.getInternal());

        // Numbers (Double)
        if (in.getCurrentLat() != null) s.setCurrentLat(in.getCurrentLat());
        if (in.getCurrentLng() != null) s.setCurrentLng(in.getCurrentLng());

        // Numbers (Integer -> entity int)
        if (in.getRatingAvg() != null) s.setRatingAvg(in.getRatingAvg());
        if (in.getActiveJobsCount() != null) s.setActiveJobsCount(in.getActiveJobsCount());

        // capacityPerDay: DTO Int -> entity String
        if (in.getCapacityPerDay() != null) s.setCapacityPerDay(String.valueOf(in.getCapacityPerDay()));

        // Booleans
        if (in.getInternal() != null) s.setInternal(in.getInternal());  // entity setter takes boolean
        if (in.getIsActive() != null) {
            // Depending on Lombok/IDEA, this might be setIsActive(Boolean) or setActive(Boolean).
            // Your field is `isActive`, so Lombok generates setIsActive(Boolean).
            s.setIsActive(in.getIsActive());
        }

        // Enums
        if (in.getStatus() != null) s.setStatus(in.getStatus());
        if (in.getSurveyorJobStatus() != null) s.setSurveyorJobStatus(in.getSurveyorJobStatus());

        // Touch updatedAt (entity is LocalDate)
        s.setUpdatedAt(LocalDate.now());

        return surveyorRepository.saveAndFlush(s);
    }


}
