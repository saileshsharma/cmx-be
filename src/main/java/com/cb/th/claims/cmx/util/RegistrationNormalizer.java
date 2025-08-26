package com.cb.th.claims.cmx.util;

public class RegistrationNormalizer {
    private RegistrationNormalizer() {
    }

    public static String normalize(String raw) {
        if (raw == null) return null;
        return raw.replace("-", "").replace(" ", "").trim().toUpperCase();
    }
}

