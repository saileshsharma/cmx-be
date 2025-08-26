package com.cb.th.claims.cmx.mapper;


import com.cb.th.claims.cmx.enums.claim.ClaimStatus;
import com.cb.th.claims.cmx.util.EnumUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {FnolDetailsMapper.class, VehicleMapper.class}, imports = {EnumUtil.class, ClaimStatus.class})
public interface ClaimMapper {
    ClaimMapper INSTANCE = Mappers.getMapper(ClaimMapper.class);
}
