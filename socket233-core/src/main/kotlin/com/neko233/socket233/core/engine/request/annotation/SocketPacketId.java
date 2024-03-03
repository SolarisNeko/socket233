package com.neko233.socket233.core.engine.request.annotation;

import java.lang.annotation.*;

/**
 * socket 通信的数据包 id
 *
 * @author LuoHaoJun on 2023-06-15
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SocketPacketId {

    // socket 某个数据包 id
    int packetId();

}