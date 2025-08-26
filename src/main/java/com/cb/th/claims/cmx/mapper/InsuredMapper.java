package com.cb.th.claims.cmx.mapper;

import com.cb.th.claims.cmx.dto.insured.InsuredDto;
import com.cb.th.claims.cmx.entity.insured.Insured;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface InsuredMapper {
    InsuredMapper INSTANCE = Mappers.getMapper(InsuredMapper.class);
    InsuredDto toDto(Insured entity);
    Insured toEntity(InsuredDto dto);
}
