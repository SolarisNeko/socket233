package com.neko233.socket233.core.engine.request.impl;

import com.neko233.socket233.core.engine.request.PacketRequestHandlerApi;
import com.neko233.socket233.core.engine.request.dto.RequestMessage;
import com.neko233.socket233.core.session.SessionApi;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class PacketRequestHandlerByDefault implements PacketRequestHandlerApi {

    @Override
    public void handleRequest(@NotNull SessionApi sessionApi, RequestMessage requestMessage) {
        log.error("没有实现处理请求. 请继承 extends AbstractPacketRequestHandler 实现处理请求");
    }

}
