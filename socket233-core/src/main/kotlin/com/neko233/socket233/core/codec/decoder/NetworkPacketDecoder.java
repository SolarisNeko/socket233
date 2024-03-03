package com.neko233.socket233.core.codec.decoder;

import com.neko233.socket233.core.engine.request.dto.RequestMessage;

/**
 * 网络包解码器
 *
 * @author LuoHaoJun on 2023-06-14
 **/
public interface NetworkPacketDecoder {

    String getDecoderName();

    /**
     * 解析 byte[] 二进制数据成上下文数据 context Map
     *
     * @param bytes data
     * @return Map
     */
    RequestMessage decode(byte[] bytes);

}
