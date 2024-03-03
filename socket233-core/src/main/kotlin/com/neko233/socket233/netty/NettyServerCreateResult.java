package com.neko233.socket233.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;

/**
 * Netty Server 创建结果
 */
public class NettyServerCreateResult {
    // boss
    public final EventLoopGroup bossGroup;
    // worker
    public final EventLoopGroup workerGroup;
    // server 启动
    public final ServerBootstrap serverBootstrap;

    public NettyServerCreateResult(EventLoopGroup bossGroup,
                                   EventLoopGroup workerGroup,
                                   ServerBootstrap serverBootstrap) {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.serverBootstrap = serverBootstrap;
    }
}