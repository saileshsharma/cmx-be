package com.cb.th.claims.cmx.mapper;


import com.cb.th.claims.cmx.dto.vehicle.VehicleDto;
import com.cb.th.claims.cmx.entity.vehicle.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {InsuredMapper.class})
public interface VehicleMapper {
    VehicleMapper INSTANCE = Mappers.getMapper(VehicleMapper.class);
    VehicleDto toDto(Vehicle entity);
    Vehicle toEntity(VehicleDto dto);
}
