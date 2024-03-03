package com.neko233.socket233.core.common;


import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * K-V API
 *
 * @author SolarisNeko 2023-03-04
 **/
public interface KvApi {

    KvApi putObject(String key,
                    Object value);

    Object getObject(String key);

    default String getString(String key) {
        Object value = getObject(key);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return String.valueOf(value);
    }

    @Nullable
    default String getFirstValue() {
        if (isEmpty()) {
            return null;
        }
        return getAllKeys().get(0);
    }


    List<String> getAllKeys();


    default String getString(String key,
                             String defaultValue) {
        String string = getString(key);
        if (string != null) {
            return string;
        }
        return defaultValue;
    }

    default boolean isContainsKey(String key) {
        String string = getString(key);
        if (string == null) {
            return false;
        }
        return true;
    }

    default boolean isEmpty() {
        return Optional.ofNullable(getAllKeys()).orElse(Collections.emptyList()).isEmpty();
    }

    // Boolean
    default Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    default Boolean getBoolean(String key,
                               Boolean defaultValue) {
        String string = getString(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(string);
    }

    // Short
    default Short getShort(String key) {
        return getShort(key, null);
    }

    default Short getShort(String key,
                           Short defaultValue) {
        String value = getString(key, String.valueOf(defaultValue));
        return Short.parseShort(value);
    }

    // Integer
    default Integer getInt(String key) {
        return getInt(key, null);
    }

    default Integer getInt(String key,
                           Integer defaultValue) {
        String value = getString(key, String.valueOf(defaultValue));
        return Integer.parseInt(value);
    }


    // Long
    default Long getLong(String key) {
        return getLong(key, null);
    }

    default Long getLong(String key,
                         Long defaultValue) {
        String string = getString(key, String.valueOf(defaultValue));
        return Long.parseLong(string);
    }

    // Float
    default Float getFloat(String key) {
        return getFloat(key, null);
    }

    default Float getFloat(String key,
                           Float defaultValue) {
        String string = getString(key, String.valueOf(defaultValue));
        return Float.parseFloat(string);
    }

    // Double
    default Double getDouble(String key) {
        return getDouble(key, null);
    }

    default Double getDouble(String key,
                             Double defaultValue) {
        String string = getString(key, String.valueOf(defaultValue));
        return Double.parseDouble(string);
    }

    // Byte
    default Byte getByte(String key) {
        return getByte(key, null);
    }

    default Byte getByte(String key,
                         Byte defaultValue) {
        String string = getString(key, String.valueOf(defaultValue));
        return Byte.parseByte(string);
    }

    // BigDecimal
    default BigDecimal getBigDecimal(String key) {
        return getBigDecimal(key, null);
    }

    default BigDecimal getBigDecimal(String key,
                                     BigDecimal defaultValue) {
        String string = getString(key, String.valueOf(defaultValue));
        return new BigDecimal(string);
    }

    // DateTime
    default LocalDateTime getDateTime(String key) {
        return getDateTime(key, null);
    }

    default LocalDateTime getDateTime(String key,
                                      LocalDateTime defaultValue) {
        String string = getString(key, String.valueOf(defaultValue));
        return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


}
