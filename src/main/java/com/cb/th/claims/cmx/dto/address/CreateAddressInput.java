package com.cb.th.claims.cmx.dto.address;

import com.cb.th.claims.cmx.enums.address.LocationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateAddressInput {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String province;
    private String postalCode;
    private String country;
    private Double latitude;   // Google Maps Lat
    private Double longitude;  // Google Maps Lng

    private String googlePlaceId;
    @Enumerated(EnumType.STRING)
    private LocationType locationType;  // ✅ Correct field
}
