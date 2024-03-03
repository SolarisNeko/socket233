package com.neko233.socket233.impl.tcp.handler;

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
import com.neko233.socket233.impl.tcp.io.TcpIoChannel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * @author LuoHaoJun on 2023-06-20
 **/
@Slf4j
public class TcpTransportLayerHandlerAdapter extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        // 生成 sessionId
        String globalSessionId = NettyNetworkUtils.getOrCreateGlobalSessionId(ctx);

        // ioChannel -> session -> registered
        final IoChannel ioChannel = new TcpIoChannel(ctx);
        final SessionApi sessionApi = NetworkSessionImplApi.from(globalSessionId, ioChannel);
        SessionManager.getInstance().addSession(globalSessionId, sessionApi);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        @Nullable final String globalSessionId = NettyAttributeUtils.get(ctx,
                SessionManager.SESSION_ATTRIBUTE_KEY_NAME,
                String.class);
        if (StringUtils.isBlank(globalSessionId)) {
            log.error("该链接没有生成 globalSessionId 就直接尝试 tcp 访问. ip = {}, port = {}",
                    NettyNetworkUtils.getRemoteIp(ctx),
                    NettyNetworkUtils.getRemotePort(ctx)
            );
            return;
        }

        RemoteHostInfo remoteHostInfo = NettyRemoteHostInfo.from(ctx);

        final ByteBuf inputByteBuf = (ByteBuf) msg;

        // 转到适配层处理
        ByteArrayData input = NettyByteArrayData.from(inputByteBuf);


        TransportLayerHandler.getInstance().handleRequest(globalSessionId, input, remoteHostInfo);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }
}
