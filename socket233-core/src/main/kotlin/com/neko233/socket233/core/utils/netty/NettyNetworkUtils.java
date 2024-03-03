package com.neko233.socket233.core.utils.netty;

import com.neko233.socket233.core.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author LuoHaoJun on 2023-06-15
 **/
public class NettyNetworkUtils {


    private static final Logger log = LoggerFactory.getLogger(NettyNetworkUtils.class);


    public static String getRemoteIp(ChannelHandlerContext ctx) {
        if (ctx == null) {
            return null;
        }
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        return inetSocketAddress.getHostName();
    }

    public static int getRemotePort(ChannelHandlerContext ctx) {
        if (ctx == null) {
            return -1;
        }
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        return inetSocketAddress.getPort();
    }


    /**
     * 生成全局 sessionId
     *
     * @param ctx netty 上下文
     * @return sessionId
     */
    public static String getOrCreateGlobalSessionId(ChannelHandlerContext ctx) {
        AttributeKey<String> key = AttributeKey.valueOf(SessionManager.SESSION_ATTRIBUTE_KEY_NAME);
        Attribute<String> attr = ctx.channel().attr(key);
        String sessionId = attr.get();
        if (StringUtils.isBlank(sessionId)) {
            String newSessionId = SessionManager.getInstance().createSessionIdString();
            attr.set(newSessionId);

            InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            String remoteIp = remoteAddress.getAddress().getHostAddress();
            int remotePort = remoteAddress.getPort();

            log.warn("收到新的连接, 连接 sessionId = {}, remoteIp = {}, port = {}",
                    newSessionId, remoteIp, remotePort);

            sessionId = newSessionId;
        }
        return sessionId;
    }
}
