package com.neko233.socket233.engine.impl.kcp;

import com.neko233.socket233.core.NetworkEngine;
import com.neko233.socket233.impl.kcp.NetworkEngineByKcpNetty;
import org.junit.Test;

public class NetworkEngineByKcpNettyTest {

    @Test
    public void kcp() throws Throwable {
        // 配置
        NetworkEngine engine = new NetworkEngineByKcpNetty();
        engine.startUp();
    }
}