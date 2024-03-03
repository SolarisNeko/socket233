package com.neko233.socket233.impl.tcp.pipeline;

import com.neko233.socket233.impl.tcp.handler.TcpTransportLayerHandlerAdapter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * @author SolarisNeko
 * Date on 2022-12-16
 */
public class TcpChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) throws Exception {

        channel.pipeline()
                .addLast(new TcpTransportLayerHandlerAdapter())
        ;
    }
}

