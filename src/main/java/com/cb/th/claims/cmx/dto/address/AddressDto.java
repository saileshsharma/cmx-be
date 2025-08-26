package com.cb.th.claims.cmx.dto.address;

import com.cb.th.claims.cmx.enums.address.LocationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressDto {
    private Long addressId;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String googlePlaceId;
    private String location;
    private LocalDateTime createdAt;

    private LocationType locationType; // ✅ match the entity field
}
