package com.neko233.socket233.impl.tcp.pipeline;


import com.neko233.socket233.impl.tcp.handler.KcpTransportLayerHandlerAdapter;
import io.jpower.kcp.netty.UkcpChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;

/**
 * @author SolarisNeko
 * Date on 2022-12-16
 */
@ChannelHandler.Sharable
public class KcpChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        UkcpChannel kcpChannel = (UkcpChannel) channel;
        // auto conv
        kcpChannel.config().setAutoSetConv(true);

        channel.pipeline()
                .addLast(new KcpTransportLayerHandlerAdapter())
        ;
    }
}

