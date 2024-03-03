package com.neko233.socket233.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class NettyServerUtils {


    @NotNull
    public static NettyServerCreateResult getNettyServerCreateResult(ChannelInitializer<Channel> channelInitializer
    ) {
        return getNettyServerCreateResult(channelInitializer, new LoggingHandler(LogLevel.INFO), null);
    }

    @NotNull
    public static NettyServerCreateResult getNettyServerCreateResult(ChannelInitializer<Channel> channelInitializer,
                                                                     LoggingHandler loggingHandler,
                                                                     Consumer<ServerBootstrap> extraConfigConsumer
    ) {
        final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        final EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);


        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                // 通道
                .channel(NioServerSocketChannel.class)
                // server 处理器
                .handler(loggingHandler)
        ;
        serverBootstrap
                // 使用池化的 ByteBuf
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                // 控制等待处理的连接队列大小
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 禁用 Nagle 算法后，数据包会立刻发送，而不会等待小数据块的累积。
                .option(ChannelOption.TCP_NODELAY, true)
                // 设置了接收缓冲区大小, 适用于高吞吐量
                .option(ChannelOption.SO_RCVBUF, 1024 * 32)
                // 设置了发送缓冲区大小, 适用于高吞吐量
                .option(ChannelOption.SO_SNDBUF, 1024 * 32);

        // 额外的配置项
        if (extraConfigConsumer != null) {
            extraConfigConsumer.accept(serverBootstrap);
        }

        serverBootstrap
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 添加自定义的Handler到Pipeline
                        pipeline.addLast(channelInitializer);
                    }
                });

        return new NettyServerCreateResult(bossGroup, workerGroup, serverBootstrap);
    }


}
