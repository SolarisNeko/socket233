package com.neko233.socket233.core.utils;


import java.util.*;
import java.util.function.BiFunction;

/**
 * Map 工具
 */
public class MapUtils {

    private MapUtils() {
    }

    /**
     * 空的 Map
     */
    public static <K, V> Map<K, V> empty() {
        return Collections.emptyMap();
    }

    public static <K, V> SortedMap<K, V> emptySortedMap() {
        return Collections.emptySortedMap();
    }


    /**
     * key = 奇数, value = 偶数
     *
     * @param objs 数组
     * @param <K>  key
     * @param <V>  value
     * @return Map
     */
    public static <K, V> Map<K, V> of(Object... objs) {
        if (objs == null) {
            return new HashMap<>(0);
        }
        if (objs.length % 2 != 0) {
            throw new IllegalArgumentException("your map data is not 2 Multiple ratio");
        }

        Map<K, V> map = new HashMap<>(objs.length / 2);
        for (int i = 0; i < objs.length; i += 2) {
            map.put((K) objs[i], (V) objs[i + 1]);
        }
        return map;
    }

    /**
     * 排序 Map
     *
     * @param comparator 比较器
     * @param objs       对象
     */
    public static <K, V> SortedMap<K, V> ofSorted(Comparator<K> comparator,
                                                  Object... objs) {
        if (objs == null) {
            return new TreeMap<>(comparator);
        }
        if (objs.length % 2 != 0) {
            throw new IllegalArgumentException("your map data is not 2 Multiple ratio");
        }

        SortedMap<K, V> map = new TreeMap<>(comparator);
        for (int i = 0; i < objs.length; i += 2) {
            map.put((K) objs[i], (V) objs[i + 1]);
        }
        return map;
    }


    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(final Map<?, ?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 合并所有 Map
     *
     * @param output      输出
     * @param biConsumer  如何合并
     * @param repeatCount 重复次数
     * @param otherMaps   其他Map
     * @return output
     */
    public static <K, V> Map<K, V> mergeAll(Map<K, V> output,
                                            BiFunction<V, V, V> biConsumer,
                                            int repeatCount,
                                            Map<K, V>... otherMaps) {
        if (repeatCount <= 0) {
            return output;
        }
        if (output == null) {
            return output;
        }
        if (biConsumer == null) {
            return output;
        }

        for (Map<K, V> kvMap : otherMaps) {
            if (kvMap == null) {
                continue;
            }
            for (int i = 0; i < repeatCount; i++) {
                kvMap.forEach((k, v) -> output.merge(k, v, biConsumer));
            }
        }
        return output;
    }

    public static <K, V> void putDataIntoList(Map<K, List<V>> map,
                                              K key,
                                              V data) {
        List<V> dataList = map.computeIfAbsent(key, k -> new ArrayList<V>());
        dataList.add(data);
    }


    /**
     * 联合相同结构的 Map
     */
    public static <K, V> Map<K, V> union(Map<K, V>... mapArray) {
        if (mapArray == null) {
            return MapUtils.empty();
        }
        HashMap<K, V> kvHashMap = new HashMap<>();
        for (Map<K, V> kvMap : mapArray) {
            if (kvMap == null) {
                continue;
            }
            kvHashMap.putAll(kvMap);
        }
        return kvHashMap;
    }


}
