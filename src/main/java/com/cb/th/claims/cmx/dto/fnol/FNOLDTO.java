package com.cb.th.claims.cmx.dto.fnol;

import com.cb.th.claims.cmx.dto.address.AddressDto;
import com.cb.th.claims.cmx.dto.policy.PolicyDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FNOLDTO {
    private Long id;
    private String incidentDescription;
    private PolicyDto policy;
    private AddressDto accidentLocation;
}