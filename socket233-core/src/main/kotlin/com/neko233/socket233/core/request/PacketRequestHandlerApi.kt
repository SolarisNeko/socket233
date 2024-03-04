package com.neko233.socket233.core.request

import com.neko233.socket233.core.request.dto.RequestMessage
import com.neko233.socket233.core.session.SessionApi

/**
 * 包请求处理器
 */
interface PacketRequestHandlerApi {

    /**
     * 处理请求 after decode 后
     */
    fun handleRequestAfterDecode(sessionApi: SessionApi, requestMessage: RequestMessage)

    /**
     * 处理请求 by 单个数据包 byte[]
     */
    fun handleRequestByByteArray(onePacketByteArray: ByteArray)

}