package com.neko233.socket233.core.codec.encoder.impl;

import com.neko233.socket233.core.codec.encoder.NetworkPacketEncoder;
import com.neko233.socket233.core.utils.JsonUtils;

import java.nio.charset.StandardCharsets;

/**
 * demo 编码器
 *
 * @author LuoHaoJun on 2023-06-16
 **/
public class DemoNetworkPacketEncoder implements NetworkPacketEncoder {

    public static NetworkPacketEncoder instance = new DemoNetworkPacketEncoder();

    @Override
    public byte[] encode(Object packetObj) {
        return JsonUtils.toJsonString(packetObj).getBytes(StandardCharsets.UTF_8);
    }
}
