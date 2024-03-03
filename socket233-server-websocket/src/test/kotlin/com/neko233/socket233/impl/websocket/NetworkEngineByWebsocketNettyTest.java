package com.neko233.socket233.impl.websocket;


import com.neko233.socket233.core.NetworkEngine;
import org.junit.Test;

public class NetworkEngineByWebsocketNettyTest {

    @Test
    public void create() throws Throwable {
        // 配置
        NetworkEngine engine = new NetworkEngineByWebsocketNetty();
        engine.startUp();

        engine.shutdown();
    }
}