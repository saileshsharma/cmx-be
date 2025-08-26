package com.cb.th.claims.cmx.dto.insured;

import com.cb.th.claims.cmx.dto.address.AddressDto;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InsuredDto {
    private Long insuredId;
    private String fullName;
    private LocalDate dob;
    private String contactNumber;
    private String email;
    private String driverLicenseNo;
    private String nationalId;
    private AddressDto address;
    private LocalDateTime createdAt;
}
