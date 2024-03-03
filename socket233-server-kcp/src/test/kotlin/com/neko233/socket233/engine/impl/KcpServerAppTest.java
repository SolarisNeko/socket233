package com.neko233.socket233.engine.impl;

import com.neko233.socket233.engine.impl.echoClient.EchoClient;
import org.junit.Test;

public class KcpServerAppTest {

    @Test
    public void demo() throws InterruptedException {
        EchoClient.run();
    }
}