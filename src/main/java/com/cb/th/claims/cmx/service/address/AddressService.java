// AddressService.java
package com.cb.th.claims.cmx.service.address;

import com.cb.th.claims.cmx.entity.address.Address;
import com.cb.th.claims.cmx.enums.address.LocationType;
import com.cb.th.claims.cmx.repository.address.AddressRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    @Data
    @AllArgsConstructor
    public static class CreateAddressCommand {
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String province;
        private String postalCode;
        private String country;
        private Double latitude;
        private Double longitude;
        private String googlePlaceId;       // ✅ String (matches DB & SDL)
        private LocationType locationType;  // may be null; we’ll default below
    }

    @Data
    @AllArgsConstructor
    public static class AddressResponse {
        private Long id;
        private String addressLine1;
        private String addressLine2;        // ✅ was wrong before
        private String city;
        private String province;
        private String postalCode;
        private String country;
        private Double latitude;
        private Double longitude;
        private String googlePlaceId;
        private LocationType locationType;
        // If you want to expose createdAt to GraphQL, add:
        // private java.time.LocalDateTime createdAt;
    }

    @Transactional
    public AddressResponse createAddress(CreateAddressCommand cmd) {

        // Provide sensible defaults if missing
        final LocationType lt = (cmd.getLocationType() != null) ? cmd.getLocationType() : LocationType.ACCIDENT_SITE;

        Address address = new Address();
        address.setAddressLine1(cmd.getAddressLine1());
        address.setAddressLine2(cmd.getAddressLine2());
        address.setCity(cmd.getCity());
        address.setProvince(cmd.getProvince());
        address.setPostalCode(cmd.getPostalCode());
        address.setCountry((cmd.getCountry() != null && !cmd.getCountry().isBlank()) ? cmd.getCountry() : "Thailand");
        address.setLatitude(cmd.getLatitude());           // use getters for consistency
        address.setLongitude(cmd.getLongitude());
        address.setGooglePlaceId(cmd.getGooglePlaceId());
        address.setLocationType(lt);

        Address saved = addressRepository.save(address);

        return new AddressResponse(saved.getId(), saved.getAddressLine1(), saved.getAddressLine2(),                  // ✅ correct field
                saved.getCity(), saved.getProvince(), saved.getPostalCode(), saved.getCountry(), saved.getLatitude(), saved.getLongitude(), saved.getGooglePlaceId(), saved.getLocationType()
                // If exposing createdAt in GraphQL selection, add saved.getCreatedAt() here
        );
    }
}
