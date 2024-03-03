package com.neko233.socket233.core.session.impl

import com.neko233.socket233.core.engine.io.IoChannel
import com.neko233.socket233.core.session.AbstractSessionApi
import com.neko233.socket233.core.session.SessionApi
import lombok.extern.slf4j.Slf4j


/**
 * 网络 Session
 */
@Slf4j
class NetworkSessionImplApi(
    globalSessionId: String,
    ioChannel: IoChannel
) :
    AbstractSessionApi(globalSessionId, ioChannel) {


    companion object {
        @JvmStatic
        fun from(globalSessionId: String, ioChannel: IoChannel): SessionApi {
            return NetworkSessionImplApi(
                globalSessionId,
                ioChannel
            )
        }
    }



    override fun isFakeSession(): Boolean {
        return false
    }

    override fun toString(): String {
        return "NetworkSessionImpl{" +
                "isValidated=" + isValidated +
                ", isWillClose=" + isWillClose +
                ", dataMap=" + dataMap +
                '}'
    }


}
