package com.neko233.socket233.core.engine.io

import com.neko233.socket233.core.bytes.ByteArrayData


@JvmDefaultWithCompatibility
interface IoChannel {

    fun getGlobalSessionId(): String

    fun getRemoteIp(): String {
        return this.getRemoteIpv4()
    }

    fun getRemoteIpv4(): String

    fun getRemotePort(): Int


    /**
     * input
     *
     * @param input 输入的字节对象
     * @return 读取的完整数据包 byte[], 需要处理好半包
     */
    fun readBodyWhenFinish(input: ByteArrayData?): ByteArray?

    /**
     * output
     *
     * @param data 写出数据
     */
    fun writeAndFlush(data: ByteArray?)

    /**
     * 关闭连接
     */
    fun close() {
        close(false)
    }

    fun close(isAsync: Boolean)
}
