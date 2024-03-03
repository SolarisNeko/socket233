package com.neko233.socket233.core.utils;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;

import java.util.List;

public class JsonUtils {

    /**
     * json String
     *
     * @param obj 对象
     * @return json string
     */
    public static String toJsonString(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static String toJsonStringPretty(Object obj) {
        return JSON.toJSONString(obj, JSONWriter.Feature.PrettyFormat);
    }

    public static <T> T parseToObject(String text,
                                      Class<T> clazz) throws Exception {
        return JSON.parseObject(text, clazz);
    }

    public static <T> List<T> parseToObjectList(String text,
                                                Class<T> objClass) throws Exception {
        return JSON.parseArray(text, objClass);
    }

    public static <T> T deserialize(String text,
                                    TypeReference<T> tf) throws Exception {
        return JSON.parseObject(text, tf);
    }

}