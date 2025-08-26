package com.cb.th.claims.cmx.repository.surveyor;

import com.cb.th.claims.cmx.entity.surveyor.Surveyor;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorJobStatus;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyorRepository extends JpaRepository<Surveyor, Long> {



    // Find by availability status
    List<Surveyor> findByStatus(SurveyorStatus status);

    // Find by job status
    List<Surveyor> findBySurveyorJobStatus(SurveyorJobStatus surveyorJobStatus);


    // Find available surveyors near a location (lat/lng range)
    List<Surveyor> findByStatusAndCurrentLatBetweenAndCurrentLngBetween(SurveyorStatus status, Double minLat, Double maxLat, Double minLng, Double maxLng);
}
