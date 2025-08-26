package com.cb.th.claims.cmx.repository.address;


import com.cb.th.claims.cmx.entity.address.Address;
import com.cb.th.claims.cmx.enums.address.LocationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {


    // ---- Identity / existence ----
    Optional<Address> findByGooglePlaceId(String googlePlaceId);

    boolean existsByGooglePlaceId(String googlePlaceId);

    void deleteByGooglePlaceId(String googlePlaceId);

    Page<Address> findByGooglePlaceIdIn(Collection<String> placeIds, Pageable pageable);

    // ---- Basic field queries ----
    Page<Address> findByCityIgnoreCase(String city, Pageable pageable);

    Page<Address> findByProvinceIgnoreCase(String province, Pageable pageable);

    Page<Address> findByPostalCodeIgnoreCase(String postalCode, Pageable pageable);

    Page<Address> findByCountryIgnoreCase(String country, Pageable pageable);

    Page<Address> findByLocationType(LocationType locationType, Pageable pageable);

    Page<Address> findByCityIgnoreCaseAndLocationType(String city, LocationType locationType, Pageable pageable);

    Page<Address> findByCountryIgnoreCaseAndLocationType(String country, LocationType locationType, Pageable pageable);

    Page<Address> findByAddressLine1ContainingIgnoreCaseOrAddressLine2ContainingIgnoreCase(String line1Like, String line2Like, Pageable pageable);

    // ---- Geo (bounding box; good DB-agnostic filter) ----
    Page<Address> findByLatitudeBetweenAndLongitudeBetween(Double minLatitude, Double maxLatitude, Double minLongitude, Double maxLongitude, Pageable pageable);

    // ---- Timestamps ----
    Page<Address> findByCreatedAtBetween(LocalDate start, LocalDate end, Pageable pageable);

    Page<Address> findByUpdatedAtAfter(LocalDate since, Pageable pageable);

    Page<Address> findByUpdatedAtBefore(LocalDate before, Pageable pageable);

    Page<Address> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Address> findTop20ByOrderByCreatedAtDesc();

    // ---- Flexible text search across common fields ----
    @Query("""
            select a from Address a
            where (:q is null or :q = ''
              or lower(a.addressLine1) like lower(concat('%', :q, '%'))
              or lower(a.addressLine2) like lower(concat('%', :q, '%'))
              or lower(a.city)         like lower(concat('%', :q, '%'))
              or lower(a.province)     like lower(concat('%', :q, '%'))
              or lower(a.postalCode)   like lower(concat('%', :q, '%'))
              or lower(a.country)      like lower(concat('%', :q, '%'))
              or lower(a.googlePlaceId)like lower(concat('%', :q, '%'))
              or cast(a.latitude as string)  like concat('%', :q, '%')
              or cast(a.longitude as string) like concat('%', :q, '%')
            )
            """)
    Page<Address> searchAllFields(@Param("q") String q, Pageable pageable);

    // ---- Aggregates ----
    long countByLocationType(LocationType locationType);

    long countByCountryIgnoreCase(String country);

    long countByCityIgnoreCase(String city);

}
