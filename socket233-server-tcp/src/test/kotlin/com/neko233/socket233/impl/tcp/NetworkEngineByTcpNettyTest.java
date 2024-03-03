package com.neko233.socket233.impl.tcp;

import com.neko233.socket233.core.NetworkEngine;
import org.junit.Test;

public class NetworkEngineByTcpNettyTest {

    @Test
    public void tcp() throws Throwable {
        // 配置
        NetworkEngine engine = new NetworkEngineByTcpNetty();
        engine.startUp();

        engine.shutdown();

    }
}