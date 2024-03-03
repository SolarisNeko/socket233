package com.neko233.socket233.core.engine.msgSequence.impl;

import com.neko233.socket233.core.engine.msgSequence.MsgSeqGenerator;
import com.neko233.socket233.core.session.SessionApi;
import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.ThreadLocalRandom;


@Getter
@ToString
public class MyMsgSeqGenerator implements MsgSeqGenerator {

    public static long generateSeed() {
        // nextLong值在计算时容易溢出，使得序号没有（伪）随机性
        // 取绝对值，因为计算时用到求余，而lua负数求余为正数，java负数求余还是负数
        return Math.abs(ThreadLocalRandom.current().nextInt());
    }

    public static final long MASK = (1L << 31);

    private double seed;

    public MyMsgSeqGenerator(long seed) {
        super();
        this.seed = seed;
    }


    @Override
    public int nextMsgSequenceId(SessionApi sessionApi) {
        seed = (seed * 1103515245L + 12345L) % MASK;
        return (int) seed;
    }


}
