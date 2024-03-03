package com.neko233.socket233.core.engine.msgSequence.impl;

import com.neko233.socket233.core.engine.msgSequence.MsgSeqGenerator;
import com.neko233.socket233.core.session.SessionApi;

public class DemoMsgSeqGenerator implements MsgSeqGenerator {

    public static final DemoMsgSeqGenerator instance = new DemoMsgSeqGenerator();

    private DemoMsgSeqGenerator() {
    }


    @Override
    public int nextMsgSequenceId(SessionApi sessionApi) {
        return 0;
    }
}