package com.neko233.socket233.core.packetmapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 消息包映射起
 */
public class PacketMappingManager {

    // <消息id, 消息 Class>
    private final Map<Integer, Class<?>> packetIdToClassMap = new HashMap<>();
    // <消息 Class, 消息id>
    private final Map<Class<?>, Integer> classToPacketIdMap = new HashMap<>();

    public void registerPacketClass(Class<?> packetClass) {
        SocketPacket annotation = packetClass.getAnnotation(SocketPacket.class);
        if (annotation != null) {
            int packetId = annotation.value();
            packetIdToClassMap.put(packetId, packetClass);
            classToPacketIdMap.put(packetClass, packetId);
        }
    }

    public Class<?> getPacketClass(int packetId) {
        return packetIdToClassMap.get(packetId);
    }

    /**
     * @param packetClass 消息包 class
     * @return 0 = error
     */
    public int getPacketId(Class<?> packetClass) {
        return classToPacketIdMap.getOrDefault(packetClass, 0);
    }

    public Set<Class<?>> getAllRegisteredClasses() {
        return classToPacketIdMap.keySet();
    }
}
