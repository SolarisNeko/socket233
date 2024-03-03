package com.neko233.socket233.core.codec.encoder;

import com.neko233.socket233.core.codec.encoder.impl.DemoNetworkPacketEncoder;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LuoHaoJun on 2023-06-19
 **/
public interface EncoderFactory {

    Map<String, NetworkPacketEncoder> encoderMap = new ConcurrentHashMap<String, NetworkPacketEncoder>() {{
        put("default", DemoNetworkPacketEncoder.instance);
    }};

    @Nullable
    static NetworkPacketEncoder get(String name) {
        return encoderMap.get(name);
    }

}
