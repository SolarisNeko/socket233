package com.neko233.socket233.core.codec.decoder;

import com.neko233.socket233.core.codec.decoder.impl.NetworkPacketDecoderByTest;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LuoHaoJun on 2023-06-19
 **/
public interface DecoderFactory {

    /**
     * <解码器名, 解码器>
     */
    Map<String, NetworkPacketDecoder> decoderNameToMap = new ConcurrentHashMap<String, NetworkPacketDecoder>() {{
        put("demo", NetworkPacketDecoderByTest.instance);
    }};

    @Nullable
    static NetworkPacketDecoder get(String name) {
        return decoderNameToMap.get(name);
    }

}
