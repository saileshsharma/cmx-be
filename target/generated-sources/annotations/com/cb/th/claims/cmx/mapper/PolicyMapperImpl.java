package com.cb.th.claims.cmx.mapper;

import com.cb.th.claims.cmx.dto.policy.PolicyDto;
import com.cb.th.claims.cmx.entity.policy.Policy;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-26T09:25:30+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class PolicyMapperImpl implements PolicyMapper {

    @Autowired
    private InsuredMapper insuredMapper;

    @Override
    public PolicyDto toDto(Policy entity) {
        if ( entity == null ) {
            return null;
        }

        PolicyDto policyDto = new PolicyDto();

        policyDto.setStartDate( entity.getStartDate() );
        policyDto.setEndDate( entity.getEndDate() );
        policyDto.setId( entity.getId() );
        policyDto.setPolicyNumber( entity.getPolicyNumber() );
        policyDto.setCoverageType( entity.getCoverageType() );
        policyDto.setPolicyStatus( entity.getPolicyStatus() );
        policyDto.setLob( entity.getLob() );
        policyDto.setPolicyType( entity.getPolicyType() );
        policyDto.setSumInsured( entity.getSumInsured() );
        policyDto.setPremium( entity.getPremium() );
        policyDto.setInsured( insuredMapper.toDto( entity.getInsured() ) );

        return policyDto;
    }

    @Override
    public Policy toEntity(PolicyDto dto) {
        if ( dto == null ) {
            return null;
        }

        Policy.PolicyBuilder policy = Policy.builder();

        policy.id( dto.getId() );
        policy.policyNumber( dto.getPolicyNumber() );
        policy.coverageType( dto.getCoverageType() );
        policy.policyStatus( dto.getPolicyStatus() );
        policy.lob( dto.getLob() );
        policy.startDate( dto.getStartDate() );
        policy.endDate( dto.getEndDate() );
        policy.policyType( dto.getPolicyType() );
        policy.sumInsured( dto.getSumInsured() );
        policy.premium( dto.getPremium() );
        policy.insured( insuredMapper.toEntity( dto.getInsured() ) );

        return policy.build();
    }
}
