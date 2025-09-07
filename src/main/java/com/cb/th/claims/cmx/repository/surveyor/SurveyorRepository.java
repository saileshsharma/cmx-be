package com.cb.th.claims.cmx.repository.surveyor;

import com.cb.th.claims.cmx.entity.surveyor.Surveyor;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorJobStatus;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorStatus;
import com.cb.th.claims.cmx.repository.surveyor.projection.SurveyorNearestRow;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyorRepository extends JpaRepository<Surveyor, Long> {
    Optional<Surveyor> findFirstByStatus(SurveyorStatus status);

    List<Surveyor> findByStatus(SurveyorStatus status);

    List<Surveyor> findBySurveyorJobStatus(SurveyorJobStatus surveyorJobStatus);

    List<Surveyor> findByStatusAndCurrentLatBetweenAndCurrentLngBetween(
            SurveyorStatus status, Double minLat, Double maxLat, Double minLng, Double maxLng
    );

    /**
     * Nearest AVAILABLE surveyors (paged) using Haversine (km).
     * NOTE: Return a projection, not entity, because we select computed columns.
     */
    @Query(value = """
            SELECT
              s.id AS id,
              s.name AS name,
              s.email AS email,
              s.phone_number AS phoneNumber,
              CAST(s.status AS TEXT) AS status,
              CAST(s.surveyor_job_status AS TEXT) AS surveyorJobStatus,
              COALESCE(s.active_jobs_count, 0) AS activeJobsCount,
              COALESCE(NULLIF(s.capacity_per_day,''), '0')::int AS capacityPerDay,
              (
                2 * 6371 * ASIN(
                  SQRT(
                    POWER(SIN(RADIANS((:lat - s.current_lat) / 2)), 2) +
                    COS(RADIANS(:lat)) * COS(RADIANS(s.current_lat)) *
                    POWER(SIN(RADIANS((:lng - s.current_lng) / 2)), 2)
                  )
                )
              ) AS distanceKm,
              COALESCE(s.skills, '') AS skills
            FROM surveyor s
            WHERE s.is_active IS TRUE
              AND s.status = 'AVAILABLE'
              AND s.current_lat IS NOT NULL
              AND s.current_lng IS NOT NULL
              AND COALESCE(NULLIF(s.capacity_per_day,''), '0') ~ '^[0-9]+$'
              AND COALESCE(s.active_jobs_count, 0) < COALESCE(NULLIF(s.capacity_per_day,''), '0')::int
            ORDER BY distanceKm ASC
            LIMIT :#{#page.pageSize} OFFSET :#{#page.offset}
            """, nativeQuery = true)
    List<SurveyorNearestRow> findNearestAvailableNative(@Param("lat") double lat,
                                                        @Param("lng") double lng,
                                                        @Param("page") Pageable page);

    /**
     * Same as above, but simpler signature with explicit :limit (handy when you don't want Pageable).
     */
    @Query(value = """
            SELECT
              s.id AS id,
              s.name AS name,
              s.email AS email,
              s.phone_number AS phoneNumber,
              CAST(s.status AS TEXT) AS status,
              CAST(s.surveyor_job_status AS TEXT) AS surveyorJobStatus,
              COALESCE(s.active_jobs_count, 0) AS activeJobsCount,
              COALESCE(NULLIF(s.capacity_per_day,''), '0')::int AS capacityPerDay,
              (
                2 * 6371 * ASIN(
                  SQRT(
                    POWER(SIN(RADIANS((:lat - s.current_lat) / 2)), 2) +
                    COS(RADIANS(:lat)) * COS(RADIANS(s.current_lat)) *
                    POWER(SIN(RADIANS((:lng - s.current_lng) / 2)), 2)
                  )
                )
              ) AS distanceKm,
              COALESCE(s.skills, '') AS skills
            FROM surveyor s
            WHERE s.is_active IS TRUE
              AND s.status = 'AVAILABLE'
              AND s.current_lat IS NOT NULL
              AND s.current_lng IS NOT NULL
              AND COALESCE(NULLIF(s.capacity_per_day,''), '0') ~ '^[0-9]+$'
              AND COALESCE(s.active_jobs_count, 0) < COALESCE(NULLIF(s.capacity_per_day,''), '0')::int
            ORDER BY distanceKm ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<SurveyorNearestRow> findNearestAvailable(@Param("lat") double lat,
                                                  @Param("lng") double lng,
                                                  @Param("limit") int limit);

    /**
     * Lock a surveyor to prevent double-assign under concurrency.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Surveyor s where s.id = :id")
    Optional<Surveyor> lockById(@Param("id") Long id);
}
