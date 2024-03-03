package com.neko233.socket233.core.session

import com.neko233.socket233.core.bytes.ByteArrayData
import com.neko233.socket233.core.codec.decoder.NetworkPacketDecoder
import com.neko233.socket233.core.codec.encoder.NetworkPacketEncoder
import com.neko233.socket233.core.engine.io.IoChannel
import com.neko233.socket233.core.engine.msgSequence.MsgSeqGenerator
import com.neko233.socket233.core.engine.msgSequence.impl.DemoMsgSeqGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import kotlin.concurrent.Volatile


abstract class AbstractSessionApi(
// 会话id
    private val globalSessionId: String,
    private val ioChannel: IoChannel,
) : SessionApi {

    // current state
    protected var closeState: Int = 0
    protected val isValidated = AtomicBoolean(false)
    protected val isWillClose = AtomicBoolean(false)

    // 会话携带的 data
    protected val dataMap: MutableMap<Class<*>, Any>

    // codec
    private var encoder: NetworkPacketEncoder? = null
    private var decoder: NetworkPacketDecoder? = null

    // ========== 实现接口 ==========
    // time state
    // 注册会话时间
    val registerTimeMs: Long

    // 最后心跳时间
    @Volatile
    var lastHeartBeatTimeMs: Long = 0

    // 最后一次触发事件
    var lastMessageTimeMs: Long = 0

    @Volatile
    var latestPingPongTimeNs: Long = 0

    // seq 生成器
    private var requestSeqGenerator: MsgSeqGenerator
    private var responseSeqGenerator: MsgSeqGenerator


    companion object {
        @JvmStatic
        private val log: Logger = LoggerFactory.getLogger(AbstractSessionApi::class.java)


        // FSM 源自更新器
        @JvmStatic
        private val closeStateAtomicUpdater =
            AtomicIntegerFieldUpdater.newUpdater(AbstractSessionApi::class.java, "closeState")

        // state 未关闭
        private const val CLOSE_STATE_UN_CLOSE = 0

        // state 关闭中
        private const val CLOSE_STATE_CLOSING = 1

        // state 已关闭
        private const val CLOSE_STATE_CLOSED = 2
    }

    /**
     * @param globalSessionId 全局会话 id
     * @param registerTimeMs  注册时间
     */
    init {
        // state
        this.closeState = CLOSE_STATE_UN_CLOSE

        // field init
        this.registerTimeMs = System.currentTimeMillis()
        this.lastHeartBeatTimeMs = this.registerTimeMs
        this.dataMap = ConcurrentHashMap()

        // seq 生成器
        this.requestSeqGenerator = DemoMsgSeqGenerator.instance
        this.responseSeqGenerator = DemoMsgSeqGenerator.instance
    }

    override fun getEncoder(): NetworkPacketEncoder? {
        return this.encoder
    }

    override fun setEncoder(encoder: NetworkPacketEncoder) {
        this.encoder = encoder
    }

    override fun getDecoder(): NetworkPacketDecoder? {
        return this.decoder
    }

    override fun setDecoder(decoder: NetworkPacketDecoder) {
        this.decoder = decoder
    }

    abstract override fun isFakeSession(): Boolean


    override fun getRemoteIp(): String {
        return ioChannel.getRemoteIp()
    }

    override fun close() {
        ioChannel.close()
    }

    override fun getRemotePort(): Int {
        return ioChannel.getRemotePort()
    }

    override fun readOneNetworkPacket(input: ByteArrayData): ByteArray? {
        return ioChannel.readBodyWhenFinish(input)
    }

    override fun getGlobalSessionId(): String {
        return globalSessionId
    }

    fun close(reason: String?): Boolean {
        if (isClosed()) {
            return false
        }
        // 将关闭, 其他线程感知
        isWillClose.set(true)
        if (trySetClosed()) {
            log.debug(
                "<AbstractSession> close session. sessionId = {}, remote ip = {}, reason = {}",
                getGlobalSessionId(),
                getRemoteIp(),
                reason
            )
            return true
        }
        return false
    }

    open val isDisableCheckIp: Boolean
        /**
         * 默认是需要检查的
         *
         * @return 是否检查 ip
         */
        get() = false

    //-------------  session 状态监测 ------------------
    fun pingPongNotExists(): Boolean {
        return this.latestPingPongTimeNs == -1L
    }

    fun updateLatestRqstTimeNS() {
        this.lastHeartBeatTimeMs = System.currentTimeMillis()
    }

    fun setIsWillClose(): Boolean {
        return isWillClose.compareAndSet(false, true)
    }

    protected fun trySetClosed(): Boolean {
        // if at closing,also mean failed
        return closeStateAtomicUpdater.compareAndSet(this, CLOSE_STATE_UN_CLOSE, CLOSE_STATE_CLOSED)
    }

    protected fun startClosingStage(): Boolean {
        return closeStateAtomicUpdater.compareAndSet(this, CLOSE_STATE_UN_CLOSE, CLOSE_STATE_CLOSING)
    }

    protected fun endClosingStage() {
        assert(this.closeState == CLOSE_STATE_CLOSING)
        closeStateAtomicUpdater.compareAndSet(this, CLOSE_STATE_CLOSING, CLOSE_STATE_CLOSED)
    }

    fun isClosingOrClosed(): Boolean {
        return closeStateAtomicUpdater[this] > CLOSE_STATE_UN_CLOSE
    }


    override fun <T> getValue(clazz: Class<T>): T {
        return dataMap[clazz] as T
    }

    override fun <T> setValue(clazz: Class<T>, value: T) {
        dataMap[clazz] = value as Any
    }

    override fun <T> setValueIfAbsent(clazz: Class<T>, value: T): T {
        return dataMap.putIfAbsent(clazz, value as Any) as T
    }


    override fun <T> removeValue(clazz: Class<T>) {
        dataMap.remove(clazz)
    }

    override fun isClosed(): Boolean {
        //保持之前的语义~，之前为atomicBoolean
        return closeStateAtomicUpdater[this] == CLOSE_STATE_CLOSED
    }

    override fun isValidated(): Boolean {
        return isValidated.get()
    }


    override fun isWillClose(): Boolean {
        return isWillClose.get()
    }

    override fun nextRequestSeqId(): Int {
        return requestSeqGenerator.nextMsgSequenceId(this)
    }


    override fun isValidated(isValidate: Boolean): Boolean {
        return isValidated.compareAndSet(!isValidate, isValidate)
    }

    override fun setRequestSeqIdGenerator(msgSeqGenerator: MsgSeqGenerator) {
        this.requestSeqGenerator = msgSeqGenerator
    }

    override fun getLastHeatBeatMs(): Long {
        return lastHeartBeatTimeMs
    }

    override fun setLastHeatBeatMs(timeMs: Long) {
        this.lastMessageTimeMs = timeMs
    }

    override fun nextResponseSeqId(): Int {
        return responseSeqGenerator.nextMsgSequenceId(this)
    }

    override fun setResponseSeqIdGenerator(msgSeqGenerator: MsgSeqGenerator) {
        this.responseSeqGenerator = msgSeqGenerator
    }

    override fun writeAndFlush(allBytes: ByteArray) {
        ioChannel.writeAndFlush(allBytes)
    }

    override fun hashCode(): Int {
        val globalSessionId = getGlobalSessionId()
        return Objects.hashCode(globalSessionId)
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj == null) {
            return false
        }
        if (javaClass != obj.javaClass) {
            return false
        }
        val other = obj as AbstractSessionApi
        return getGlobalSessionId() == other.getGlobalSessionId()
    }


}
