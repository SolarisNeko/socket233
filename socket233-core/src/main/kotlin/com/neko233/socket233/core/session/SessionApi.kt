package com.neko233.socket233.core.session

import com.neko233.socket233.core.bytes.ByteArrayData
import com.neko233.socket233.core.codec.decoder.NetworkPacketDecoder
import com.neko233.socket233.core.codec.encoder.NetworkPacketEncoder
import com.neko233.socket233.core.engine.msgSequence.MsgSeqGenerator
import com.neko233.socket233.core.utils.JsonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import java.util.function.Function


interface SessionApi {

    val LOGGER: Logger
        get() = LoggerFactory.getLogger(SessionApi::class.java)

    /**
     * 全局会话id
     *
     * @return sessionId
     */
    fun getGlobalSessionId(): String

    fun isFakeSession(): Boolean

    fun getRemoteIp(): String

    fun getEncoder(): NetworkPacketEncoder?

    fun setEncoder(encoder: NetworkPacketEncoder)

    fun getDecoder(): NetworkPacketDecoder?

    fun setDecoder(decoder: NetworkPacketDecoder)

    fun <T> getValue(clazz: Class<T>): T

    fun <T> setValue(clazz: Class<T>, value: T)

    fun <T> removeValue(clazz: Class<T>)

    fun <T> setValueIfAbsent(clazz: Class<T>, value: T): T {
        val obj = getValue(clazz)
        if (obj == null) {
            setValue(clazz, value)
            return value
        }
        return obj
    }

    fun <T> getOrCreate(clazz: Class<T>, creator: Function<Class<*>, T>): T {
        val value = getValue(clazz)
        if (value != null) {
            return value
        }
        val apply = creator.apply(clazz)
        setValue(clazz, apply)
        return apply
    }

    /**
     * 判断会话是否关闭，关闭的会话将不再处理上下行消息
     *
     * @return true is close,false none close
     */
    fun isClosed(): Boolean

    /**
     * 判断会话是否验证过
     *
     * @return true is validate,false none validate
     */
    fun isValidated(): Boolean

    /**
     * @param isValidate 已校验
     * @return 是否已校验
     */
    fun isValidated(isValidate: Boolean): Boolean

    /**
     * @return 设置已校验过
     */
    fun setHaveValidated(): Boolean {
        return isValidated(true)
    }

    /**
     * 判断会话是否即将关闭，即将关闭的会话将不再处理上行消息
     *
     * @return true is willClose, false none willClose
     */
    fun isWillClose(): Boolean

    /**
     * request 请求序列生成器
     *
     * @return 下一个请求 seqId
     */
    fun nextRequestSeqId(): Int

    fun setRequestSeqIdGenerator(msgSeqGenerator: MsgSeqGenerator)


    /**
     * response 请求序列生成器
     *
     * @return 下一个响应 seqId
     */
    fun nextResponseSeqId(): Int

    fun setResponseSeqIdGenerator(msgSeqGenerator: MsgSeqGenerator)


    /**
     * 最后一次心跳时间戳 (上行/下行/心跳) 都会刷新这个心跳时间
     *
     * @return epochTimeMs
     */
    fun getLastHeatBeatMs(): Long

    fun setLastHeatBeatMs(timeMs: Long)

    /**
     * @return 是否已经过期
     */
    fun isExpireSession(): Boolean {
        val currentTimMs = System.currentTimeMillis()
        val diffHeartBeatMs = currentTimMs - this.getLastHeatBeatMs()
        return diffHeartBeatMs > HEART_BEAT_TIMEOUT_MS
    }


    /**
     * 刷新心跳时间
     */
    fun refreshHeartBeatEpochTimeMs() {
        val epochTimeMs = System.currentTimeMillis()
        setLastHeatBeatMs(epochTimeMs)
    }

    fun close()

    fun getRemotePort(): Int

    /**
     * 读取一个完整的网络包
     *
     * @param input 输入
     * @return 数据二进制
     */
    fun readOneNetworkPacket(input: ByteArrayData): ByteArray?

    fun sendPacket(packetObj: Any) {
        val encoder = this.getEncoder()
        if (encoder == null) {
            val json = JsonUtils.toJsonString(
                packetObj
            )
            LOGGER.debug(
                "没有编码器可以处理这个 packet 进行发送.  class={}, packetObj = {}",
                packetObj::class.java,
                json
            )
            return
        }
        val bytes: ByteArray? = encoder.encode(packetObj)
        if (bytes == null) {
            val json = JsonUtils.toJsonString(
                packetObj
            )
            LOGGER.debug("编码器没有编码成功. class={}, packetObj = {}", packetObj::class.java, json)
            return
        }
        this.writeAndFlush(bytes)

    }

    /**
     * 最原始的 byte[] 网络输出
     * ps: 一般不使用. 一般用于测试
     *
     * @param allBytes 所有字节
     */
    fun writeAndFlush(allBytes: ByteArray)

    fun writeAndFlush(content: String) {
        writeAndFlush(content.toByteArray(StandardCharsets.UTF_8))
    }

    companion object {
        @JvmStatic
        // 默认心跳过期时长 = 30s
        val HEART_BEAT_TIMEOUT_MS: Long = TimeUnit.SECONDS.toMillis(30)
    }
}