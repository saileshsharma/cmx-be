package com.cb.th.claims.cmx.mapper;

import com.cb.th.claims.cmx.dto.fnol.FNOLDTO;
import com.cb.th.claims.cmx.entity.fnol.FNOL;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {PolicyMapper.class, AddressMapper.class})
public interface FNOLMapper {
    FNOLDTO toDto(FNOL fnol);
    FNOL toEntity(FNOLDTO dto);
}