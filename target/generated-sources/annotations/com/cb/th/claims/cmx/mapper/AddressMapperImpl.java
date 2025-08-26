package com.cb.th.claims.cmx.mapper;

import com.cb.th.claims.cmx.dto.address.AddressDto;
import com.cb.th.claims.cmx.entity.address.Address;
import com.cb.th.claims.cmx.enums.address.LocationType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-26T13:40:11+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class AddressMapperImpl implements AddressMapper {

    @Override
    public AddressDto toDto(Address entity) {
        if ( entity == null ) {
            return null;
        }

        AddressDto addressDto = new AddressDto();

        if ( entity.getLocationType() != null ) {
            addressDto.setLocation( entity.getLocationType().name() );
        }
        addressDto.setCity( entity.getCity() );
        addressDto.setPostalCode( entity.getPostalCode() );
        addressDto.setCountry( entity.getCountry() );
        addressDto.setGooglePlaceId( entity.getGooglePlaceId() );
        addressDto.setCreatedAt( entity.getCreatedAt() );
        addressDto.setLocationType( entity.getLocationType() );

        return addressDto;
    }

    @Override
    public Address toEntity(AddressDto dto) {
        if ( dto == null ) {
            return null;
        }

        Address.AddressBuilder address = Address.builder();

        if ( dto.getLocation() != null ) {
            address.locationType( Enum.valueOf( LocationType.class, dto.getLocation() ) );
        }
        address.city( dto.getCity() );
        address.postalCode( dto.getPostalCode() );
        address.country( dto.getCountry() );
        address.googlePlaceId( dto.getGooglePlaceId() );
        address.createdAt( dto.getCreatedAt() );

        return address.build();
    }
}
