package com.cb.th.claims.cmx.service.fnol;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * FNOL reference generator backed by a PostgreSQL sequence to avoid collisions across nodes.
 *
 * Prereqs:
 *   CREATE SEQUENCE IF NOT EXISTS fnol_ref_seq;
 *   -- And ensure a unique index on the column that stores this reference:
 *   -- CREATE UNIQUE INDEX IF NOT EXISTS ux_fnol_reference_no ON fnol(fnol_reference_no);
 *
 * Format: FNOL-YYMMDD-00001
 *   - Prefix: "FNOL-"
 *   - Date:   Local date in Asia/Singapore (or server TZ) formatted yyMMdd
 *   - Seq:    Next value from 'fnol_ref_seq', zero-padded to PAD_WIDTH
 */
@Component
@RequiredArgsConstructor
public class DbSequenceFnolReferenceGenerator implements FnolReferenceGenerator {

    private final JdbcTemplate jdbc;

    private static final String PREFIX = "CHU";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyMMdd");
    private static final int PAD_WIDTH = 5; // change to 6/7 if you want more digits

    @Override
    public String next() {
        // 1) Grab next number from DB sequence (atomic and cluster-safe)
        final Long seq = jdbc.queryForObject("SELECT nextval('fnol_ref_seq')", Long.class);

        // 2) Build formatted string: FNOL-YYMMDD-00001
        final String datePart = LocalDate.now().format(DATE_FMT);
        final String seqPart  = leftPad(seq == null ? 1L : seq, PAD_WIDTH);

        return PREFIX + datePart + seqPart;
    }

    private static String leftPad(long n, int width) {
        final String s = Long.toString(n);
        if (s.length() >= width) return s;                  // don't trim if it grows beyond width
        final StringBuilder b = new StringBuilder(width);
        for (int i = s.length(); i < width; i++) b.append('0');
        return b.append(s).toString();
    }
}
