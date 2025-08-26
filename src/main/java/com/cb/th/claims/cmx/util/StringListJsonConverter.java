package com.cb.th.claims.cmx.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.List;

@Converter
public class StringListJsonConverter implements AttributeConverter<List<String>, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Override public String convertToDatabaseColumn(List<String> attribute) {
        try { return MAPPER.writeValueAsString(attribute == null ? Collections.emptyList() : attribute); }
        catch (Exception e) { throw new IllegalArgumentException("Failed to serialize tags", e); }
    }
    @Override public List<String> convertToEntityAttribute(String dbData) {
        try { return dbData == null || dbData.isBlank()
                ? Collections.emptyList()
                : MAPPER.readValue(dbData, new TypeReference<List<String>>() {}); }
        catch (Exception e) { throw new IllegalArgumentException("Failed to deserialize tags", e); }
    }
}