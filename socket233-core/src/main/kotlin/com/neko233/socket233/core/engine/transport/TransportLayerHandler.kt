package com.neko233.socket233.core.engine.transport

import com.neko233.socket233.core.bytes.ByteArrayData
import com.neko233.socket233.core.codec.decoder.DecoderFactory
import com.neko233.socket233.core.codec.decoder.NetworkPacketDecoder
import com.neko233.socket233.core.codec.encoder.EncoderFactory
import com.neko233.socket233.core.codec.encoder.NetworkPacketEncoder
import com.neko233.socket233.core.engine.NetworkEngineContext
import com.neko233.socket233.core.engine.common.host.RemoteHostInfo
import com.neko233.socket233.core.engine.request.PacketRequestHandlerApi
import com.neko233.socket233.core.session.SessionManager
import com.neko233.socket233.core.utils.JsonUtils
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean


/**
 * 传输层处理器 <br></br>
 * 此处主要是：<br></br>
 * 请求 = byte[] -> requestMessageContext  <br></br>
 * 响应 = returnValue + routePathHandlerConfig -> byte[] | 并控制如何发送  <br></br>
 */
@Slf4j
class TransportLayerHandler private constructor() {
    // 是否初始化
    private val isInit = AtomicBoolean(false)

    // 是否初始化 error?
    private val isInitError = AtomicBoolean(false)

    // 解码器 = byte[] -> object
    private var messageDecoder: NetworkPacketDecoder? = null

    // 编码器 = object -> byte[]
    private var messageEncoder: NetworkPacketEncoder? = null

    // encoder 名字
    private var encoderName: String? = null
    // decoder 名字
    private var decoderName: String? = null

    companion object {
        @JvmStatic
        val instance: TransportLayerHandler = TransportLayerHandler()

        private val LOGGER: Logger = LoggerFactory.getLogger(TransportLayerHandler::class.java)!!
    }

    /**
     * 烂初始化
     */
    private fun initLazy() {
        if (isInit.get()) {
            return
        }

        synchronized(TransportLayerHandler::class.java) {
            // first init
            if (isInit.compareAndSet(false, true)) {
                // decoder
                this.decoderName =
                    NetworkEngineContext.instance.defaultNetworkEngine.decoderName
                this.messageDecoder = DecoderFactory.get(decoderName)

                // encoder
                this.encoderName =
                    NetworkEngineContext.instance.defaultNetworkEngine.encoderName
                this.messageEncoder = EncoderFactory.get(encoderName)
            }
            checkInit()
        }
    }


    /**
     * 处理请求
     *
     * @param globalSessionId 会话id
     * @param reqBytes        bytes
     * @param remoteHostInfo  远程主机信息
     */
    fun handleRequest(
        globalSessionId: String,
        reqBytes: ByteArrayData,
        remoteHostInfo: RemoteHostInfo?
    ) {
        initLazy()

        if (isInitError.get()) {
            LOGGER.error("初始化错误. 无法处理请求")
            return
        }


        // sessionId 换取 ioChannel
        val sessionApi = SessionManager.instance.getSession(globalSessionId)
        if (sessionApi == null) {
            LOGGER.error(
                "websocket 已断开连接. remote host info = {}",
                JsonUtils.toJsonString(remoteHostInfo)
            )
            return
        }

        // 刷新 session
        SessionManager.instance.refreshSession(globalSessionId)


        try {
            // 返回内容一定是完整的包
            val onePacketByteArray = sessionApi.readOneNetworkPacket(reqBytes)
                ?: return

            // 解码出请求的消息
            val reqMessage = messageDecoder!!.decode(onePacketByteArray)
            if (reqMessage == null) {
                LOGGER.error(
                    "解析 body -> message context 失败. body string utf8 = {}",
                    String(onePacketByteArray)
                )
                return
            }

            // 路由到业务处理
            val router = NetworkEngineContext.instance.getSingletonNotNull(
                PacketRequestHandlerApi::class.java
            )
            router.handleRequest(sessionApi, reqMessage)
        } catch (e: Throwable) {
            LOGGER.error("数据分包->解析->执行. 报错了", e)
        } finally {
        }
    }

    private fun checkInit() {
        if (messageDecoder == null) {
            LOGGER.error(
                "初始化 decoder 为空! 请检查 decoder name = {}",
                this.decoderName
            )
            isInitError.set(true)
        }
        if (messageDecoder == null) {
            LOGGER.error(
                "初始化 encoder 为空! 请检查 encoder name = {}",
                this.encoderName
            )
            isInitError.set(true)
        }
    }


}
