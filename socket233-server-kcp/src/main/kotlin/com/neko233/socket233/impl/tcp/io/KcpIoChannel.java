package com.neko233.socket233.impl.tcp.io;


import com.neko233.socket233.core.bytes.ByteArrayData;
import com.neko233.socket233.core.engine.common.host.NettyRemoteHostInfo;
import com.neko233.socket233.core.engine.common.host.RemoteHostInfo;
import com.neko233.socket233.core.engine.io.IoChannel;
import com.neko233.socket233.core.msgpacket.MessagePacketHandler;
import com.neko233.socket233.impl.tcp.packet.KcpSplitEndMessagePacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author LuoHaoJun on 2023-06-21
 **/
@Slf4j
public class KcpIoChannel implements IoChannel {

    // 粘包分包处理器
    private final MessagePacketHandler messagePacketHandler = new KcpSplitEndMessagePacketHandler();

    // netty 通信 io 上下文
    private final ChannelHandlerContext nettyContext;
    private final InetSocketAddress inetSocketAddress;


    public KcpIoChannel(ChannelHandlerContext nettyContext) {
        this.nettyContext = nettyContext;
        this.inetSocketAddress = (InetSocketAddress) nettyContext.channel().remoteAddress();
    }

    @Override
    public String getGlobalSessionId() {
        AttributeKey<String> key = AttributeKey.valueOf("session");
        Attribute<String> attr = nettyContext.attr(key);
        if (attr == null) {
            return null;
        }
        return attr.get();
    }

    @Override
    public String getRemoteIpv4() {
        return inetSocketAddress.getHostName();
    }

    @Override
    public int getRemotePort() {
        return inetSocketAddress.getPort();
    }

    @Override
    public byte[] readBodyWhenFinish(ByteArrayData input) {
        if (input == null) {
            return null;
        }

        return messagePacketHandler.handle(input);
    }


    private static byte[] getBytes(ByteArrayData byteArray, int msgLength) {
        return byteArray.readBytes(msgLength);
    }


    @Override
    public void writeAndFlush(byte[] outBytes) {
        if (outBytes == null) {
            return;
        }

        ByteBuf outputByteBuf = nettyContext.alloc().buffer(outBytes.length);
        outputByteBuf.writeBytes(outBytes);

        // write callback Promise
        ChannelFuture writeFuture = nettyContext.writeAndFlush(outputByteBuf);
        writeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    Throwable cause = future.cause();
                    // 处理失败的原因
                    RemoteHostInfo from = NettyRemoteHostInfo.from(nettyContext);
                    log.error("write and flush to remote host 失败. remote host = {}", from.toJsonString(), cause);
                }
            }
        });

    }


    @Override
    public void close(boolean isAsync) {
        ChannelFuture close = this.nettyContext.close();
        if (isAsync) {
            return;
        }
        try {
            close.sync();
        } catch (Exception e) {
            log.error("同步关闭连接失败! ", e);
        }
    }
}




