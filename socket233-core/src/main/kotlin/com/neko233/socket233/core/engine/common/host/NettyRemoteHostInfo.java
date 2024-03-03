package com.neko233.socket233.core.engine.common.host;

import com.neko233.socket233.core.utils.JsonUtils;
import com.neko233.socket233.core.utils.MapUtils;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author LuoHaoJun on 2023-06-15
 **/
public class NettyRemoteHostInfo implements RemoteHostInfo {

    private final InetSocketAddress inetSocketAddress;


    public static RemoteHostInfo from(ChannelHandlerContext ctx) {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        return new NettyRemoteHostInfo((InetSocketAddress) socketAddress);
    }

    private NettyRemoteHostInfo(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    @Override
    public String getRemoteIpv4() {
        return inetSocketAddress.getAddress().getHostAddress();
    }

    @Override
    public String getRemoteIpv6() {
        return inetSocketAddress.getAddress().getHostAddress();
    }

    @Override
    public int getRemotePort() {
        return inetSocketAddress.getPort();
    }

    @Override
    public String toJsonString() {
        return JsonUtils.toJsonString(
                MapUtils.of(
                        "ip", getRemoteIpv4(),
                        "port", getRemotePort()
                )
        );
    }
}
