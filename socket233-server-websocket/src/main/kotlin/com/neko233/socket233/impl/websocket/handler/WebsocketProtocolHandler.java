package com.neko233.socket233.impl.websocket.handler;

import com.neko233.socket233.core.bytes.ByteArrayData;
import com.neko233.socket233.core.bytes.bytes_impl.NettyByteArrayData;
import com.neko233.socket233.core.engine.common.host.NettyRemoteHostInfo;
import com.neko233.socket233.core.engine.common.host.RemoteHostInfo;
import com.neko233.socket233.core.engine.io.IoChannel;
import com.neko233.socket233.core.engine.transport.TransportLayerHandler;
import com.neko233.socket233.core.session.SessionApi;
import com.neko233.socket233.core.session.SessionManager;
import com.neko233.socket233.core.session.impl.NetworkSessionImplApi;
import com.neko233.socket233.core.utils.netty.NettyAttributeUtils;
import com.neko233.socket233.core.utils.netty.NettyNetworkUtils;
import com.neko233.socket233.impl.websocket.channel.NettyWebSocketIoChannel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ws 协议处理器
 */
public class WebsocketProtocolHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(WebsocketProtocolHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);

        // 生成 sessionId
        String globalSessionId = NettyNetworkUtils.getOrCreateGlobalSessionId(ctx);

        // ioChannel -> session -> registered
        final IoChannel ioChannel = new NettyWebSocketIoChannel(ctx);
        final SessionApi sessionApi = NetworkSessionImplApi.from(globalSessionId, ioChannel);
        SessionManager.getInstance().addSession(globalSessionId, sessionApi);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        @Nullable final String globalSessionId = NettyAttributeUtils.get(ctx,
                SessionManager.SESSION_ATTRIBUTE_KEY_NAME,
                String.class);
        if (StringUtils.isBlank(globalSessionId)) {
            log.error("该链接没有生成 globalSessionId 就直接尝试 websocket 访问. ip = {}, port = {}",
                    NettyNetworkUtils.getRemoteIp(ctx),
                    NettyNetworkUtils.getRemotePort(ctx)
            );
            return;
        }

        RemoteHostInfo remoteHostInfo = NettyRemoteHostInfo.from(ctx);

        if (!(msg instanceof WebSocketFrame)) {
            log.debug("This is not a WebSocket frame");
            log.debug("Client Channel : " + ctx.channel());
            return;
        }

        // ws frame
        if (msg instanceof BinaryWebSocketFrame) {
            ByteBuf inputByteBuf = ((BinaryWebSocketFrame) msg).content();

            // 转到适配层处理
            ByteArrayData input = NettyByteArrayData.from(inputByteBuf);
            TransportLayerHandler.getInstance().handleRequest(globalSessionId, input, remoteHostInfo);

            log.debug("BinaryWebSocketFrame Received binary content = {} ", inputByteBuf);
        } else if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame textWebSocketFrame = ((TextWebSocketFrame) msg);
            ByteBuf inputByteBuf = textWebSocketFrame.content();

            // 转到适配层处理
            ByteArrayData input = NettyByteArrayData.from(inputByteBuf);
            TransportLayerHandler.getInstance().handleRequest(globalSessionId, input, remoteHostInfo);

        } else if (msg instanceof PingWebSocketFrame) {
            log.debug("PingWebSocketFrame Received : ");
            log.debug("ping content = {}", ((PingWebSocketFrame) msg).content());
        } else if (msg instanceof PongWebSocketFrame) {
            log.debug("PongWebSocketFrame Received : ");
            log.debug("pong content = {}", ((PongWebSocketFrame) msg).content());
        } else if (msg instanceof CloseWebSocketFrame) {
            log.debug("CloseWebSocketFrame Received : ");
            log.debug("ReasonText :" + ((CloseWebSocketFrame) msg).reasonText());
            log.debug("StatusCode : " + ((CloseWebSocketFrame) msg).statusCode());
        } else {
            log.debug("Unsupported WebSocketFrame");
        }
    }


    /**
     * 取消注册 = 关闭连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        // session 退出
        String sessionId = NettyAttributeUtils.get(ctx, "session", String.class);
        SessionManager.getInstance().removeSession(sessionId);
        super.channelUnregistered(ctx);

    }
}