package com.deneme.influencerinsight.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public final class JsonUtils {

    private JsonUtils() {
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final TypeReference<Map<String, Object>> MAP_REF = new TypeReference<>() {
    };
    private static final TypeReference<List<Map<String, Object>>> LIST_MAP_REF = new TypeReference<>() {
    };

    /**
     * Map içinden güvenli String okuma
     */
    public static String getString(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object v = map.get(key);
        return (v == null) ? null : String.valueOf(v);
    }

    /**
     * Map içinden güvenli String okuma (default ile)
     */
    public static String getStringOrDefault(Map<String, Object> map, String key, String def) {
        String v = getString(map, key);
        return (v == null || v.isBlank()) ? def : v;
    }

    /**
     * Objeyi tip güvenli Map<String,Object>’a çevir
     */
    public static Map<String, Object> toMap(Object obj) {
        if (obj == null) return Map.of();
        if (obj instanceof Map<?, ?> raw) {
            return MAPPER.convertValue(raw, MAP_REF);
        }
        return Map.of();
    }

    /**
     * Objeyi tip güvenli List<Map<String,Object>>’a çevir
     */
    public static List<Map<String, Object>> toListOfMaps(Object obj) {
        if (obj == null) return List.of();
        if (obj instanceof List<?> raw) {
            return MAPPER.convertValue(raw, LIST_MAP_REF);
        }
        return List.of();
    }

    /**
     * Nesneyi JSON string’e çevir (hata varsa "{}")
     */
    public static String toJson(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            return "{}";
        }
    }
}
