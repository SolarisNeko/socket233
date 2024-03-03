package com.neko233.socket233.core.common;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LuoHaoJun on 2023-06-19
 **/
public class Kv implements KvApi {

    private final Map<String, Object> map = new HashMap<>();

    @Override
    public List<String> getAllKeys() {
        return new ArrayList<>(map.keySet());
    }


    @Override
    public KvApi putObject(String s, Object o) {
        map.put(s, o);
        return this;
    }

    @Override
    public Object getObject(String s) {
        return map.get(s);
    }


    public Kv put(String k, String v) {
        map.put(k, v);
        return this;
    }
}
