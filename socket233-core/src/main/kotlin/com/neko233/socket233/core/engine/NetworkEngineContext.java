package com.neko233.socket233.core.engine;

import com.neko233.socket233.core.NetworkEngine;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * @author LuoHaoJun on 2023-06-19
 **/
@Slf4j
public class NetworkEngineContext {

    // singleton
    public static final NetworkEngineContext instance = new NetworkEngineContext();

    public static final String KEY_DEFAULT = "default";

    private final Map<String, NetworkEngine> networkEngineMap = new HashMap<>();
    // 组件容器
    private static final Map<Class<?>, Object> singletonComponentMap = new ConcurrentHashMap<>();

    public NetworkEngine getDefaultNetworkEngine() {
        return networkEngineMap.get("default");
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getSingleton(Class<T> clazz) {
        return (T) singletonComponentMap.get(clazz);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <T> T getSingletonNotNull(Class<T> clazz) {
        T t = (T) singletonComponentMap.get(clazz);
        if (t == null) {
            // runtime exception
            throw new IllegalArgumentException("没有找到 class = " + clazz.getName() + " 的 singleton");
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    public <T> void setSingleton(Class<T> clazz, T obj) {
        singletonComponentMap.put(clazz, obj);
    }

    /**
     * 如果不存在, 才设置 singleton 到容器中
     */
    @SuppressWarnings("unchecked")
    public <T> void setSingletonIfAbsent(Class<T> clazz, T obj) {
        singletonComponentMap.computeIfAbsent(clazz, k -> obj);
    }

    /**
     * 注册引擎管理中
     *
     * @param engine 引擎
     * @return this
     */
    public NetworkEngineContext register(NetworkEngine engine) {
        networkEngineMap.merge(engine.getEngineName(), engine, (v1, v2) -> {
            log.error("引擎名字出现重复. 不进行覆盖. engine name = {}", v1.getEngineName());
            return v1;
        });
        return this;
    }


    public NetworkEngineContext merge(String key,
                                      NetworkEngine engine,
                                      BiFunction<NetworkEngine, NetworkEngine, NetworkEngine> biFunction) {
        networkEngineMap.merge(key, engine, biFunction);
        return this;
    }


    public NetworkEngine getNetworkEngine(String networkEngineName) {
        return networkEngineMap.get(networkEngineName);
    }

}
