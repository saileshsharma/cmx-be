package com.cb.th.claims.cmx.mapper;

import com.cb.th.claims.cmx.dto.address.AddressDto;
import com.cb.th.claims.cmx.entity.address.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")   // ✅ REQUIRED for Spring Bean
public interface AddressMapper
{
    @Mapping(source = "locationType", target = "location")
    AddressDto toDto(Address entity);

    @Mapping(source = "location", target = "locationType")
    Address toEntity(AddressDto dto);

}

