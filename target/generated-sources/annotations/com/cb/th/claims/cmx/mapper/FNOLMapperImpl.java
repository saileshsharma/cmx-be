package com.cb.th.claims.cmx.mapper;

import com.cb.th.claims.cmx.dto.fnol.FNOLDTO;
import com.cb.th.claims.cmx.entity.fnol.FNOL;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-26T09:25:30+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class FNOLMapperImpl implements FNOLMapper {

    @Autowired
    private PolicyMapper policyMapper;
    @Autowired
    private AddressMapper addressMapper;

    @Override
    public FNOLDTO toDto(FNOL fnol) {
        if ( fnol == null ) {
            return null;
        }

        FNOLDTO.FNOLDTOBuilder fNOLDTO = FNOLDTO.builder();

        fNOLDTO.id( fnol.getId() );
        fNOLDTO.incidentDescription( fnol.getIncidentDescription() );
        fNOLDTO.policy( policyMapper.toDto( fnol.getPolicy() ) );
        fNOLDTO.accidentLocation( addressMapper.toDto( fnol.getAccidentLocation() ) );

        return fNOLDTO.build();
    }

    @Override
    public FNOL toEntity(FNOLDTO dto) {
        if ( dto == null ) {
            return null;
        }

        FNOL fNOL = new FNOL();

        fNOL.setId( dto.getId() );
        fNOL.setIncidentDescription( dto.getIncidentDescription() );
        fNOL.setPolicy( policyMapper.toEntity( dto.getPolicy() ) );
        fNOL.setAccidentLocation( addressMapper.toEntity( dto.getAccidentLocation() ) );

        return fNOL;
    }
}
