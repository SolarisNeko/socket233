package com.neko233.socket233.impl.tcp;

import com.neko233.socket233.core.NetworkEngine;
import com.neko233.socket233.core.common.Kv;
import com.neko233.socket233.core.engine.exception.EngineStartUpException;
import com.neko233.socket233.core.env.EngineEnvKeys;
import com.neko233.socket233.core.utils.CloseableUtils;
import com.neko233.socket233.core.utils.ResourcesJdkUtils;
import com.neko233.socket233.impl.tcp.pipeline.TcpChannelInitializer;
import com.neko233.socket233.netty.NettyServerCreateResult;
import com.neko233.socket233.netty.NettyServerUtils;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.net.BindException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
public class NetworkEngineByTcpNetty implements NetworkEngine {

    private final AtomicBoolean isStartUp = new AtomicBoolean(false);

    @Override
    public void init() throws Throwable {

    }

    @Override
    public void create() throws Throwable {
        long startMs = System.currentTimeMillis();

        // other thread run
        Thread serverStartupThread = new Thread(() -> {
            log.info("tcp Netty Server is going to start up.");
            try {
                // blocking
                startUpNettyWebsocket();
            } catch (Exception e) {
                handleStartUpException(e);
            }

        });
        serverStartupThread.setName("tcp-netty-server-thread-main");
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
            log.warn("启动 engine = {} 成功! 耗时 = {} ms", getEngineName(), (endMs - startMs));
        }
    }

    private void startUpNettyWebsocket() {

        checkConfigKv();
        final int port = getPort();

        final NettyServerCreateResult result = NettyServerUtils.getNettyServerCreateResult(new TcpChannelInitializer());

        // Configure the server.
        try {

            ChannelFuture sync = result.serverBootstrap.bind(port).sync();

            isStartUp.set(true);
            sync.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            log.error("启动 netty tcp 失败", e);
        } finally {
            result.bossGroup.shutdownGracefully();
            result.workerGroup.shutdownGracefully();
        }
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
            String msg = String.format("启动 NettyWebsocketNetworkEngine 报错. port 被占用 = %s",
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
        return "netty-tcp";
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