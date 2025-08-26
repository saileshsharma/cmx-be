package com.cb.th.claims.cmx.mapper;

import com.cb.th.claims.cmx.dto.fnol.FnolDetailsDto;
import com.cb.th.claims.cmx.entity.fnol.FnolDetail;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-26T13:40:11+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class FnolDetailsMapperImpl implements FnolDetailsMapper {

    @Override
    public FnolDetailsDto toDto(FnolDetail entity) {
        if ( entity == null ) {
            return null;
        }

        FnolDetailsDto fnolDetailsDto = new FnolDetailsDto();

        return fnolDetailsDto;
    }

    @Override
    public FnolDetail toEntity(FnolDetailsDto dto) {
        if ( dto == null ) {
            return null;
        }

        FnolDetail.FnolDetailBuilder fnolDetail = FnolDetail.builder();

        return fnolDetail.build();
    }
}
