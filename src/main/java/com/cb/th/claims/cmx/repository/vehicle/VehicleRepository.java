package com.cb.th.claims.cmx.repository.vehicle;


import com.cb.th.claims.cmx.entity.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {


    /* ============
     * Identity / existence
     * ============ */
    Optional<Vehicle> findByVin(String vin);

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    boolean existsByVin(String vin);

    boolean existsByRegistrationNumber(String registrationNumber);

    boolean existsByEngineNo(String engineNo);

    void deleteByVin(String vin);

    void deleteByRegistrationNumber(String registrationNumber);

    List<Vehicle> findByVinIn(Collection<String> vins);

    /* ============
     * Make / model / year
     * ============ */
    List<Vehicle> findByMakeIgnoreCase(String make);

    List<Vehicle> findByModelIgnoreCase(String model);

    List<Vehicle> findByMakeIgnoreCaseAndModelIgnoreCase(String make, String model);

    List<Vehicle> findByYear(Integer year);

    List<Vehicle> findByYearBetween(Integer startYear, Integer endYear);

    List<Vehicle> findByYearGreaterThanEqual(Integer minYear);

    List<Vehicle> findByYearIn(Collection<Integer> years);

    /* ============
     * Specs & attributes
     * ============ */
    List<Vehicle> findByColorIgnoreCase(String color);

    List<Vehicle> findByBodyTypeIgnoreCase(String bodyType);

    List<Vehicle> findByFuelTypeIgnoreCase(String fuelType);

    List<Vehicle> findByUsageTypeIgnoreCase(String usageType);

    /* ============
     * Ownership
     * ============ */
    List<Vehicle> findByOwnerNameContainingIgnoreCase(String ownerNameLike);

    List<Vehicle> findByOwnerContactContainingIgnoreCase(String ownerContactLike);

    /* ============
     * Registration geography
     * ============ */
    List<Vehicle> findByRegistrationStateIgnoreCase(String registrationState);

    /* ============
     * Engine / fraud-related lookups
     * ============ */
    Optional<Vehicle> findByEngineNo(String engineNo);

    /* ============
     * Timestamps
     * ============ */
    List<Vehicle> findByCreatedAtBetween(LocalDate start, LocalDate end);

    List<Vehicle> findByUpdatedAtAfter(LocalDate since);

    List<Vehicle> findByUpdatedAtBefore(LocalDate before);

    List<Vehicle> findTop10ByOrderByCreatedAtDesc();

    /* ============
     * Aggregates
     * ============ */
    long countByUsageTypeIgnoreCase(String usageType);

    long countByFuelTypeIgnoreCase(String fuelType);

    long countByRegistrationStateIgnoreCase(String registrationState);


    // Normalize on the DB side (UPPER + strip '-' and spaces) so no schema change needed
    @Query("""
                select v from Vehicle v
                where upper(replace(replace(v.registrationNumber, '-', ''), ' ', '')) = :normalized
            """)
    Optional<Vehicle> findByRegistrationNormalized(String normalized);


    @Query("""
              select v from Vehicle v
              where regexp_replace(lower(v.registrationNumber), '\\s|-', '', 'g')
                    = regexp_replace(lower(:rawPlate), '\\s|-', '', 'g')
            """)
    Optional<Vehicle> findByLicensePlate(String rawPlate);


}
