package com.neko233.socket233.impl.websocket.pipeline;

import com.neko233.socket233.impl.websocket.handler.HttpUpgradeToWebsocketProtocolHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author SolarisNeko
 * Date on 2022-12-16
 */
public class WebSocketConfigChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast("httpServerCodec", new HttpServerCodec())
                .addLast("httpServerHandler", new HttpUpgradeToWebsocketProtocolHandler())
        ;
    }
}

