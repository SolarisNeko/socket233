package com.neko233.socket233.core.session.impl

import com.neko233.socket233.core.bytes.ByteArrayData
import com.neko233.socket233.core.engine.io.impl.IoChannelForTest
import com.neko233.socket233.core.session.AbstractSessionApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class FakeSessionImplApi(globalSessionId: String) : AbstractSessionApi(globalSessionId, IoChannelForTest()) {
    private val fakeIp = "127.0.0.1"

    companion object {
        private val log: Logger = LoggerFactory.getLogger(FakeSessionImplApi::class.java)
    }

    override fun getRemoteIp(): String {
        return fakeIp
    }

    override fun close() {
    }

    override fun getRemotePort(): Int {
        return 0
    }

    override fun readOneNetworkPacket(input: ByteArrayData): ByteArray? {
        return input.readBytes()
    }

    override fun writeAndFlush(allBytes: ByteArray) {
        log.info("write data = {}", String(allBytes))
    }

    override fun isFakeSession(): Boolean {
        return true
    }


    override val isDisableCheckIp: Boolean
        get() = true

}
