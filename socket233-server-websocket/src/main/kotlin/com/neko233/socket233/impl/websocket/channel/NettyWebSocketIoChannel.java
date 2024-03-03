package com.neko233.socket233.impl.websocket.channel;

import com.neko233.socket233.core.bytes.ByteArrayData;
import com.neko233.socket233.core.engine.common.host.NettyRemoteHostInfo;
import com.neko233.socket233.core.engine.common.host.RemoteHostInfo;
import com.neko233.socket233.core.engine.io.IoChannel;
import com.neko233.socket233.core.msgpacket.AllMessagePacketHandler;
import com.neko233.socket233.core.msgpacket.MessagePacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyWebSocketIoChannel implements IoChannel {

    // 粘包分包处理器
    private final MessagePacketHandler messagePacketHandler = new AllMessagePacketHandler();

    // netty 通信 io 上下文
    private final ChannelHandlerContext nettyContext;
    private final InetSocketAddress inetSocketAddress;


    public NettyWebSocketIoChannel(ChannelHandlerContext nettyContext) {
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

        ByteBuf buffer = nettyContext.alloc().buffer(outBytes.length);
        buffer.writeBytes(outBytes);

        // to ws frame : binary
        BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(buffer);
        // netty all is callback
        ChannelFuture writeFuture = nettyContext.writeAndFlush(binaryWebSocketFrame);

        ChannelPromise writePromise = nettyContext.newPromise();

        writeFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                // 这个释放会导致资源错乱, 不建议使用
//                binaryWebSocketFrame.release();
                writePromise.setSuccess();
            } else {
                // 输出失败
                RemoteHostInfo from = NettyRemoteHostInfo.from(nettyContext);
                log.error("write and flush to remote host 失败. remote host = {}", from.toJsonString());

                writePromise.setFailure(future.cause());
            }
        });

        // TODO
        // Wait for write operation to complete
//        try {
//            writePromise.await();
//        } catch (InterruptedException e) {
//            log.error("response write and flush async await 报错 ", e);
//        }


    }


    @Override
    public void close(boolean isAsync) {
        ChannelFuture close = this.nettyContext.close();
        if (isAsync) {
            return;
        }
        try {
            close.sync();
        } catch (InterruptedException e) {
            log.error("同步关闭连接失败! ");
        }
    }
}
