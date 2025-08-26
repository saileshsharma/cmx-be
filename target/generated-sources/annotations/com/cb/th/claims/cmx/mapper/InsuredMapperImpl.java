package com.cb.th.claims.cmx.mapper;

import com.cb.th.claims.cmx.dto.insured.InsuredDto;
import com.cb.th.claims.cmx.entity.insured.Insured;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import javax.annotation.processing.Generated;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-26T09:25:30+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class InsuredMapperImpl implements InsuredMapper {

    @Autowired
    private AddressMapper addressMapper;
    private final DatatypeFactory datatypeFactory;

    public InsuredMapperImpl() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch ( DatatypeConfigurationException ex ) {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public InsuredDto toDto(Insured entity) {
        if ( entity == null ) {
            return null;
        }

        InsuredDto insuredDto = new InsuredDto();

        insuredDto.setDob( entity.getDob() );
        insuredDto.setEmail( entity.getEmail() );
        insuredDto.setDriverLicenseNo( entity.getDriverLicenseNo() );
        insuredDto.setNationalId( entity.getNationalId() );
        insuredDto.setAddress( addressMapper.toDto( entity.getAddress() ) );
        insuredDto.setCreatedAt( xmlGregorianCalendarToLocalDateTime( localDateToXmlGregorianCalendar( entity.getCreatedAt() ) ) );

        return insuredDto;
    }

    @Override
    public Insured toEntity(InsuredDto dto) {
        if ( dto == null ) {
            return null;
        }

        Insured.InsuredBuilder insured = Insured.builder();

        insured.dob( dto.getDob() );
        insured.nationalId( dto.getNationalId() );
        insured.email( dto.getEmail() );
        insured.driverLicenseNo( dto.getDriverLicenseNo() );
        insured.createdAt( xmlGregorianCalendarToLocalDate( localDateTimeToXmlGregorianCalendar( dto.getCreatedAt() ) ) );
        insured.address( addressMapper.toEntity( dto.getAddress() ) );

        return insured.build();
    }

    private XMLGregorianCalendar localDateToXmlGregorianCalendar( LocalDate localDate ) {
        if ( localDate == null ) {
            return null;
        }

        return datatypeFactory.newXMLGregorianCalendarDate(
            localDate.getYear(),
            localDate.getMonthValue(),
            localDate.getDayOfMonth(),
            DatatypeConstants.FIELD_UNDEFINED );
    }

    private XMLGregorianCalendar localDateTimeToXmlGregorianCalendar( LocalDateTime localDateTime ) {
        if ( localDateTime == null ) {
            return null;
        }

        return datatypeFactory.newXMLGregorianCalendar(
            localDateTime.getYear(),
            localDateTime.getMonthValue(),
            localDateTime.getDayOfMonth(),
            localDateTime.getHour(),
            localDateTime.getMinute(),
            localDateTime.getSecond(),
            localDateTime.get( ChronoField.MILLI_OF_SECOND ),
            DatatypeConstants.FIELD_UNDEFINED );
    }

    private static LocalDate xmlGregorianCalendarToLocalDate( XMLGregorianCalendar xcal ) {
        if ( xcal == null ) {
            return null;
        }

        return LocalDate.of( xcal.getYear(), xcal.getMonth(), xcal.getDay() );
    }

    private static LocalDateTime xmlGregorianCalendarToLocalDateTime( XMLGregorianCalendar xcal ) {
        if ( xcal == null ) {
            return null;
        }

        if ( xcal.getYear() != DatatypeConstants.FIELD_UNDEFINED
            && xcal.getMonth() != DatatypeConstants.FIELD_UNDEFINED
            && xcal.getDay() != DatatypeConstants.FIELD_UNDEFINED
            && xcal.getHour() != DatatypeConstants.FIELD_UNDEFINED
            && xcal.getMinute() != DatatypeConstants.FIELD_UNDEFINED
        ) {
            if ( xcal.getSecond() != DatatypeConstants.FIELD_UNDEFINED
                && xcal.getMillisecond() != DatatypeConstants.FIELD_UNDEFINED ) {
                return LocalDateTime.of(
                    xcal.getYear(),
                    xcal.getMonth(),
                    xcal.getDay(),
                    xcal.getHour(),
                    xcal.getMinute(),
                    xcal.getSecond(),
                    Duration.ofMillis( xcal.getMillisecond() ).getNano()
                );
            }
            else if ( xcal.getSecond() != DatatypeConstants.FIELD_UNDEFINED ) {
                return LocalDateTime.of(
                    xcal.getYear(),
                    xcal.getMonth(),
                    xcal.getDay(),
                    xcal.getHour(),
                    xcal.getMinute(),
                    xcal.getSecond()
                );
            }
            else {
                return LocalDateTime.of(
                    xcal.getYear(),
                    xcal.getMonth(),
                    xcal.getDay(),
                    xcal.getHour(),
                    xcal.getMinute()
                );
            }
        }
        return null;
    }
}
