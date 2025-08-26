package com.cb.th.claims.cmx.dto.fnol;

import com.cb.th.claims.cmx.dto.claim.ClaimDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FnolResponseDto {
    private FnolDetailsDto fnol;
    private ClaimDto claim;
}
