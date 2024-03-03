package com.neko233.socket233.impl.websocket.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * HTTP --> ws 协议
 *
 * @author SolarisNeko
 * Date on 2022-12-16
 */
@Slf4j
public class HttpUpgradeToWebsocketProtocolHandler extends ChannelInboundHandlerAdapter {

    WebSocketServerHandshaker handShaker;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {


        if (!(msg instanceof HttpRequest)) {
            log.error("Incoming request is unknown");
            return;
        }

        HttpRequest httpRequest = (HttpRequest) msg;

        HttpHeaders headers = httpRequest.headers();
//        log.debug("Connection : " + headers.get("Connection"));
//        log.debug("Upgrade : " + headers.get("Upgrade"));

        // connection & upgrade
        if ("Upgrade".equalsIgnoreCase(headers.get(HttpHeaderNames.CONNECTION))
                && "WebSocket".equalsIgnoreCase(headers.get(HttpHeaderNames.UPGRADE))
        ) {

            // Adding new handler to the existing pipeline to handle WebSocket Messages
            ctx.pipeline()
                    .replace(this, "websocketHandler", new WebsocketProtocolHandler());

            // WebSocketHandler added to the pipeline. replace httpServerHandler
            this.handleWebsocketHandshake(ctx, httpRequest);

            // Handshake is done


        } else {
            InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            String ip = remoteAddress.getAddress().getHostAddress();
            int port = remoteAddress.getPort();
            log.error("[HttpServerHandler] Remote request not a HTTP for websocket connection. ip ={}, port = {} "
                    , ip, port);
            return;
        }


    }


    /**
     * Do the handshaking for WebSocket request
     */
    protected void handleWebsocketHandshake(ChannelHandlerContext ctx, HttpRequest req) {
        String wsUrl = getWebSocketURL(req);
        log.debug("request websocket URL = {}", wsUrl);

        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(wsUrl, null, true);
        // 握手
        handShaker = wsFactory.newHandshaker(req);
        if (handShaker == null) {
            // 不支持该版本
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handShaker.handshake(ctx.channel(), req);
        }
    }

    protected String getWebSocketURL(HttpRequest req) {
        return "ws://" + req.headers().get("Host") + req.uri();
    }
}