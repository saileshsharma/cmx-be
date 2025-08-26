package com.cb.th.claims.cmx.mapper;


import com.cb.th.claims.cmx.dto.policy.PolicyDto;
import com.cb.th.claims.cmx.entity.policy.Policy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {InsuredMapper.class})
public interface PolicyMapper {
    PolicyMapper INSTANCE = Mappers.getMapper(PolicyMapper.class);
    PolicyDto toDto(Policy entity);
    Policy toEntity(PolicyDto dto);
}
