package com.cb.th.claims.cmx.mapper;


import com.cb.th.claims.cmx.dto.fnol.FnolDetailsDto;
import com.cb.th.claims.cmx.entity.fnol.FnolDetail;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring",
        uses = {PolicyMapper.class, VehicleMapper.class, InsuredMapper.class, AddressMapper.class})
public interface FnolDetailsMapper {
    FnolDetailsMapper INSTANCE = Mappers.getMapper(FnolDetailsMapper.class);
    FnolDetailsDto toDto(FnolDetail entity);
    FnolDetail toEntity(FnolDetailsDto dto);
}
