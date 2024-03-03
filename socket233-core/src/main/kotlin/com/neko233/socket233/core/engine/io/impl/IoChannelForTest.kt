package com.neko233.socket233.core.engine.io.impl

import com.neko233.socket233.core.bytes.ByteArrayData
import com.neko233.socket233.core.engine.io.IoChannel

class IoChannelForTest : IoChannel {
    override fun getGlobalSessionId(): String {
        return ""
    }

    override fun getRemoteIpv4(): String {
        return ""
    }

    override fun getRemotePort(): Int {
        return 0
    }

    override fun readBodyWhenFinish(input: ByteArrayData?): ByteArray? {
        return ByteArray(16)
    }

    override fun writeAndFlush(data: ByteArray?) {
        if (data == null) {
            return
        }
        println("输出数据 byteArray. utf8 string = ${String(data, Charsets.UTF_8)}")
    }

    override fun close(isAsync: Boolean) {
        println("开始关闭")
    }
}