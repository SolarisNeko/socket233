package com.neko233.socket233.core.env;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author solarisNeko on 2023-06-21
 **/
public class Socket233Env {

    private static final Map<String, String> envMap = new ConcurrentHashMap<>();

    public static void set(String key, String value) {
        envMap.put(key, value);
        System.setProperty(key, value);
    }

    public static void set(Map<String, String> map) {
        map.forEach(Socket233Env::set);
    }

    public static String get(String key) {
        String value = envMap.get(key);
        if (value == null) {
            value = System.getProperty(key);
        }
        return value;
    }

    public static String getOrDefault(String key, String defaultValue) {
        String value = Socket233Env.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }


    public static final String ENGINE_NAME = "engine-1.0.0";

    public static final String ENGINE_VERSION = "1.0.0";

    public static final String ENGINE_AUTHOR = "XXXXXXXXX";

    public static final String ENGINE_EMAIL = "XXXXXXXXXXXXXXXX";

    public static final String ENGINE_WEBSITE = "https://github.com/solarisNeko/engine";

    public static final String ENGINE_LICENSE = "Apache License 2.0";

    public static final String ENGINE_LICENSE_URL = "https://github.com/solarisNeko/engine/blob/master/LICENSE";

    public static final String ENGINE_HOMEPAGE = "https://github.com/solarisNeko/engine";

    public static final String ENGINE_DESCRIPTION = "A uni-socket233 for Java";

    public static final String ENGINE_BUILD_DATE = "2023-06-01";
}
