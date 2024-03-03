package com.neko233.socket233.impl.websocket;

import com.neko233.socket233.core.NetworkEngine;
import com.neko233.socket233.core.common.Kv;
import com.neko233.socket233.core.engine.exception.EngineStartUpException;
import com.neko233.socket233.core.utils.ResourcesJdkUtils;
import com.neko233.socket233.impl.websocket.pipeline.WebSocketConfigChannelInitializer;
import com.neko233.socket233.netty.NettyServerCreateResult;
import com.neko233.socket233.netty.NettyServerUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.BindException;
import java.security.cert.CertificateException;
import java.util.Properties;


@Slf4j
public class NetworkEngineByWebsocketNetty implements NetworkEngine {

    @Override
    public void init() throws Throwable {

    }

    @Override
    public void create() throws Throwable {

        // other thread run
        Thread wsThread = new Thread(() -> {
            log.info("websocket Netty Server is going to start up.");
            try {
                startUpNettyWebsocket();
            } catch (Exception e) {
                handleStartUpException(e);
            }
            log.info("websocket Netty Server is started");
        });
        wsThread.setName("websocket-netty-server-thread-main");
        wsThread.start();
    }

    private void startUpNettyWebsocket() throws CertificateException, SSLException {

        checkConfigKv();
        final Boolean isHaveSsl = getConfigKv().getBoolean("server.isHaveSsl");
        final Integer port = getPort();

        // Configure SSL.
        final SslContext sslCtx;
        if (isHaveSsl) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }


        // Configure the server.
        NettyServerCreateResult result = NettyServerUtils.getNettyServerCreateResult(new WebSocketConfigChannelInitializer());
        ServerBootstrap serverBootstrap = result.serverBootstrap;
        EventLoopGroup bossGroup = result.bossGroup;
        EventLoopGroup workerGroup = result.workerGroup;

        try {


            final Channel ch = serverBootstrap.bind(port)
                    .sync()
                    .channel();

            log.info("启动 websocket server 成功! port = {}", port);

            logServerStartupSuccess(serverBootstrap);

            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动 netty websocket 失败", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    private static void logServerStartupSuccess(ServerBootstrap serverBootstrap) {
        serverBootstrap.config().options().get(ChannelOption.MAX_MESSAGES_PER_WRITE);

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
        try {
            File resourceConfigFile;
            if (configFilePath.startsWith("absolute:")) {
                // 绝对路径协议
                String filePath = configFilePath.replaceFirst("absolute:", "");
                resourceConfigFile = new File(filePath);
            } else {
                // 默认, resources/ 目录下
                resourceConfigFile = ResourcesJdkUtils.getResourceFile(configFilePath);
            }
            if (resourceConfigFile == null) {
                return kv;
            }

            // 不存在
            if (!resourceConfigFile.exists()) {
                log.error("配置文件不存在! file full path = {}", resourceConfigFile.getAbsolutePath());
                return kv;
            }

            // properties 读取
            final Properties properties = new Properties();
            properties.load(new FileReader(resourceConfigFile));
            properties.forEach((k, v) -> {
                kv.put(String.valueOf(k), String.valueOf(v));
            });
            return kv;
        } catch (IOException e) {
            log.error("read config file error.", e);
            return kv;
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
        return "netty-websocket";
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