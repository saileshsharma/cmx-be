package com.cb.th.claims.cmx.util;

public class EnumUtil {
    public static String enumToString(Enum<?> e) {
        return (e != null) ? e.name() : null;
    }
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T stringToEnum(String value, Class<T> enumType) {
        if (value == null || value.isEmpty()) return null;
        return Enum.valueOf(enumType, value.toUpperCase());
    }
}
