package com.neko233.socket233.core.request.impl;

import com.neko233.socket233.core.request.PacketRequestHandlerApi;
import com.neko233.socket233.core.request.dto.RequestMessage;
import com.neko233.socket233.core.session.SessionApi;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

public class PacketRequestHandlerByDefault implements PacketRequestHandlerApi {

    @Override
    public void handleRequestAfterDecode(@NotNull SessionApi sessionApi, RequestMessage requestMessage) {
        System.err.println("[PacketRequestHandlerByDefault] 没有实现处理请求. 请继承 extends AbstractPacketRequestHandler 实现处理请求");
    }

    @Override
    public void handleRequestByByteArray(@NotNull byte[] onePacketByteArray) {
        System.err.println("[PacketRequestHandlerByDefault] 没有实现处理请求. 请继承 extends AbstractPacketRequestHandler 实现处理请求");
    }
}
