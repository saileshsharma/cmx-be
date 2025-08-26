package com.cb.th.claims.cmx.util;

import com.cb.th.claims.cmx.exception.BusinessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeParsers {
    private static final DateTimeFormatter ISO_LOCAL_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private TimeParsers() {
    }

    public static LocalDateTime parseAccidentDateOrThrow(String raw) {
        try {
            if (raw.contains("T") && (raw.endsWith("Z") || raw.matches(".*[+-]\\d\\d:\\d\\d$"))) {
                return OffsetDateTime.parse(raw).toLocalDateTime();
            }
            if (raw.contains("T")) {
                return LocalDateTime.parse(raw, ISO_LOCAL_DATE_TIME);
            }
            return LocalDate.parse(raw, ISO_LOCAL_DATE).atStartOfDay();
        } catch (DateTimeParseException ex) {
            throw new BusinessException("ACCIDENT_DATE_INVALID", "Invalid accidentDate format. Use 'YYYY-MM-DD' or ISO-8601 like 'YYYY-MM-DDTHH:mm:ss[Z|+08:00]'.", 400);
        }
    }
}