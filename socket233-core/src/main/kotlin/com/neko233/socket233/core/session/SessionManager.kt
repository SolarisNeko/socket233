package com.neko233.socket233.core.session

import com.neko233.socket233.core.utils.CollectionUtils.isEmpty
import com.neko233.socket233.core.utils.JsonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong


/**
 * @author LuoHaoJun on 2023-06-15
 */
class SessionManager {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(SessionManager::class.java)

        // 单例子
        @JvmStatic
        val instance: SessionManager = SessionManager()

        // 会话属性 name
        const val SESSION_ATTRIBUTE_KEY_NAME: String = "session"

        // 会话id生成器
        private val sessionIdCounter = AtomicLong(1)

        // sessionId : Session Map | 定期检查 session 是否过期
        private val globalSessionIdToIoChannalMap: MutableMap<String, SessionApi> = ConcurrentHashMap()
    }

    init {
        // heartbeat check
        startHeartBeatCheckScheduler()
    }


    /**
     * 开始心跳检查 scheduler
     */
    private fun startHeartBeatCheckScheduler() {
        val scheduler = Executors.newScheduledThreadPool(1)
        scheduler.scheduleAtFixedRate({
            val toRemoveSessionIdSet: MutableSet<String> = HashSet()
            for ((sessionId, sessionApi) in globalSessionIdToIoChannalMap) {
                // 是否过期
                if (sessionApi.isExpireSession()) {
                    toRemoveSessionIdSet.add(sessionId)
                    continue
                }
            }

            if (isEmpty<String>(toRemoveSessionIdSet)) {
                return@scheduleAtFixedRate
            }

            log.warn(
                "以下 sessionId 已过期, 并断开连接, 自动清理掉. sessionId list = {}",
                JsonUtils.toJsonString(toRemoveSessionIdSet)
            )

            // close
            for (sessionId in toRemoveSessionIdSet) {
                val sessionApi = globalSessionIdToIoChannalMap.remove(sessionId) ?: continue
                sessionApi.close()

                log.warn(
                    "session closed. sessionId = {}, ip = {}, port = {}",
                    sessionId, sessionApi.getRemoteIp(), sessionApi.getRemotePort()
                )
            }
        }, 10, 30, TimeUnit.SECONDS)
    }


    /**
     * 创建 session id
     *
     * @return sessionId
     */
    fun createSessionId(): Long {
        return sessionIdCounter.getAndIncrement()
    }

    fun createSessionIdString(): String {
        return createSessionId().toString()
    }

    fun getSession(globalSessionId: String): SessionApi? {
        return globalSessionIdToIoChannalMap[globalSessionId]
    }

    fun addSession(sessionId: String, sessionApi: SessionApi) {
        globalSessionIdToIoChannalMap[sessionId] = sessionApi

        log.info(
            "new session. sessionId = {}, remote IP = {}, port = {}",
            sessionId,
            sessionApi.getRemoteIp(),
            sessionApi.getRemoteIp()
        )
    }

    fun removeSession(sessionId: String) {
        val remove = globalSessionIdToIoChannalMap.remove(sessionId)
            ?: // 已移除
            return
        remove.close()
    }


    fun refreshSession(globalSessionId: String) {
        val sessionApi = globalSessionIdToIoChannalMap[globalSessionId] ?: return
        sessionApi.refreshHeartBeatEpochTimeMs()
    }

}
