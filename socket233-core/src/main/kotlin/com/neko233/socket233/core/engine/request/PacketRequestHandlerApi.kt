package com.neko233.socket233.core.engine.request

import com.neko233.socket233.core.engine.request.dto.RequestMessage
import com.neko233.socket233.core.session.SessionApi

interface PacketRequestHandlerApi {


    /**
     * 处理请求
     */
    fun handleRequest(sessionApi: SessionApi, requestMessage: RequestMessage)
}