package com.neko233.socket233.core.codec.encoder;


import org.jetbrains.annotations.Nullable;

/**
 * 网络消息宝编码器
 *
 * @author LuoHaoJun on 2023-06-16
 **/
public interface NetworkPacketEncoder {

    /**
     * 编码
     *
     * @param packetObj 返回对象
     * @return byte[]
     */
    @Nullable
    byte[] encode(Object packetObj);

}
