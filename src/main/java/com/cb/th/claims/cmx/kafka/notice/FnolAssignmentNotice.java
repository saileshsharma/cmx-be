package com.cb.th.claims.cmx.kafka.notice;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FnolAssignmentNotice {
    private String fnolReferenceNo;
    private String status;     // e.g. "SENT_TO_SURVEYOR"
    private String message;    // e.g. "FNOL has been sent to surveyor"
    private OffsetDateTime timestamp;
}