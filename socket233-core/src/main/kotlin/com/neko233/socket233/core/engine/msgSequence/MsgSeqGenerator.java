package com.neko233.socket233.core.engine.msgSequence;

import com.neko233.socket233.core.session.SessionApi;

/**
 * 消息序号生成器
 */
public interface MsgSeqGenerator {

    default int nextMsgSequenceId() {
        return nextMsgSequenceId(null);
    }

    int nextMsgSequenceId(SessionApi sessionApi);

}