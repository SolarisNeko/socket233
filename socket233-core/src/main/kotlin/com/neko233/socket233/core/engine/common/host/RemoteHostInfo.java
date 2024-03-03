package com.neko233.socket233.core.engine.common.host;

/**
 * @author LuoHaoJun on 2023-06-15
 **/
public interface RemoteHostInfo {

    String getRemoteIpv4();

    String getRemoteIpv6();

    int getRemotePort();


    String toJsonString();

}
