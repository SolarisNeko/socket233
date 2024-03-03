package com.neko233.socket233.impl.kcp;

import com.neko233.socket233.core.common.Kv;
import com.neko233.socket233.core.NetworkEngine;
import com.neko233.socket233.core.engine.exception.EngineStartUpException;
import com.neko233.socket233.core.env.EngineEnvKeys;
import com.neko233.socket233.core.env.Socket233Env;
import com.neko233.socket233.core.utils.CloseableUtils;
import com.neko233.socket233.core.utils.ResourcesJdkUtils;
import com.neko233.socket233.impl.tcp.pipeline.KcpChannelInitializer;
import io.jpower.kcp.netty.ChannelOptionHelper;
import io.jpower.kcp.netty.UkcpChannelOption;
import io.jpower.kcp.netty.UkcpServerChannel;
import io.netty.bootstrap.UkcpServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.net.BindException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class NetworkEngineByKcpNetty implements NetworkEngine {

    private static final Logger log = LoggerFactory.getLogger(NetworkEngineByKcpNetty.class);


    private final AtomicBoolean isStartUp = new AtomicBoolean(false);

    @Override
    public void init() throws Throwable {
        Socket233Env.set(EngineEnvKeys.KEY_ENGINE_NAME, getEngineName());

    }

    @Override
    public void create() throws Throwable {

        long startMs = System.currentTimeMillis();

        // other thread run
        Thread serverStartupThread = new Thread(() -> {
            log.info("tcp Netty Server is going to start up.");
            try {
                // blocking
                runKcpServer();
            } catch (Exception e) {
                handleStartUpException(e);
            }

        });
        serverStartupThread.setName("kcp-netty-server-thread-main");
        serverStartupThread.start();

        // blocking wait start-up success
        int count = 0;
        while (!isStartUp.get()) {
            if (count > 9999) {
                log.error("启动服务器有问题!");
                break;
            }
            TimeUnit.MILLISECONDS.sleep(10);
            count++;
        }

        long endMs = System.currentTimeMillis();
        if (isStartUp.get()) {
            log.warn("启动 engine = {} 成功! 耗时 = {} ms, port = {}",
                    getEngineName(),
                    (endMs - startMs),
                    getPort()
            );
        }
    }

    /**
     * 运行 KCP 引擎
     */
    private void runKcpServer() {

        checkConfigKv();

        final Integer port = getPort();

        final EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);

        // Configure the server.
        try {
            // server
            UkcpServerBootstrap serverBootstrap = new UkcpServerBootstrap();
            serverBootstrap
                    // just worker, no boss
                    .group(workerGroup)
                    // Ukcp 的网络
                    .channel(UkcpServerChannel.class)
                    .childHandler(new KcpChannelInitializer());
            // 网络连接选项
            ChannelOptionHelper.nodelay(serverBootstrap,
                            true,
                            20,
                            2,
                            true
                    )
                    .childOption(UkcpChannelOption.UKCP_MTU, 512);

            ChannelFuture sync = serverBootstrap.bind(port).sync();

            isStartUp.set(true);
            sync.channel().closeFuture().sync();

            logServerStartupSuccess(serverBootstrap);

        } catch (InterruptedException e) {
            log.error("启动 netty UKCP 失败", e);
        } finally {
            workerGroup.shutdownGracefully();
        }


    }

    private static void logServerStartupSuccess(UkcpServerBootstrap serverBootstrap) {
        log.info("Server startUp success");

    }

    @Override
    public void destroy() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public Kv getConfigKv() {
        String configFilePath = getConfigFilePath();
        Kv kv = new Kv();
        final Properties properties = new Properties();
        FileReader reader = null;
        try {
            File resourceFile = ResourcesJdkUtils.getResourceFile(configFilePath);
            reader = new FileReader(resourceFile);

            properties.load(reader);
            properties.forEach((k, v) -> kv.put(String.valueOf(k), String.valueOf(v)));

            return kv;
        } catch (Exception e) {
            log.error("read config file error.", e);
            return kv;
        } finally {
            CloseableUtils.close(reader);
        }
    }


    @Override
    public void handleStartUpException(Throwable e) {
        if (e instanceof BindException) {
            String msg = String.format(
                    "启动 NettyWebsocketNetworkEngine 报错. port 被占用 = %s",
                    this.getPort());
            throw new EngineStartUpException(e, msg);
        }
        throw new EngineStartUpException(e, "启动 NettyWebsocketNetworkEngine 报错. 没匹配到错误原因");
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public String getEngineName() {
        return "netty-kcp";
    }

    @Override
    public String getConfigFilePath() {
        return System.getProperty(EngineEnvKeys.KEY_ENGINE_CONFIG, "socket233.properties");
    }

    @Override
    public void checkConfigKv() {
        final Integer port = getConfigKv().getInt("server.port");
        if (port == null) {
            log.error("port is null!");
            throw new IllegalArgumentException();
        }
    }
}