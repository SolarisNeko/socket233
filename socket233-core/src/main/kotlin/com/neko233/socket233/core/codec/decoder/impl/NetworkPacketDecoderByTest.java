package com.neko233.socket233.core.codec.decoder.impl;

import com.neko233.socket233.core.codec.decoder.NetworkPacketDecoder;
import com.neko233.socket233.core.request.dto.RequestMessage;
import com.neko233.socket233.core.utils.MapUtils;

import java.nio.charset.StandardCharsets;

/**
 * demo 解码器
 *
 * @author LuoHaoJun on 2023-06-14
 **/
public class NetworkPacketDecoderByTest implements NetworkPacketDecoder {

    public static final NetworkPacketDecoder instance = new NetworkPacketDecoderByTest();

    private NetworkPacketDecoderByTest() {

    }

    @Override
    public String getDecoderName() {
        return "demo";
    }

    @Override
    public RequestMessage decode(byte[] bytes) {
        return RequestMessage.builder()
                .originalByteArray(bytes)
                // 测试用的 packetId
                .packetId(0)
                .dataMap(MapUtils.of(
                        "data", new String(bytes, StandardCharsets.UTF_8)
                ))
                .build();
    }

}
