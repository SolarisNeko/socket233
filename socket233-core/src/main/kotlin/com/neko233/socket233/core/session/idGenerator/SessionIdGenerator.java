package com.neko233.socket233.core.session.idGenerator;

import java.util.concurrent.atomic.AtomicInteger;

public class SessionIdGenerator {

    private static final AtomicInteger ioSessionIdGen = new AtomicInteger(0);
    private static final AtomicInteger fakeSessionIdGen = new AtomicInteger(0);

    public static int generateIOSessionId() {
        //保证恒>0
        int sessionId = ioSessionIdGen.incrementAndGet();
        if (sessionId <= 0) {
            synchronized (ioSessionIdGen) {
                int tSessionId = ioSessionIdGen.get();
                if (tSessionId <= 0) {
                    ioSessionIdGen.set(1);
                    sessionId = 1;
                } else {
                    sessionId = ioSessionIdGen.incrementAndGet();
                }
            }
        }
        return sessionId;
    }

    public static int generateFakeSessionId() {
        //保证恒 < 0, 且不等于 Integer.minValue,(minValue,0)
        int sessionId = fakeSessionIdGen.decrementAndGet();
        if (sessionId == Integer.MIN_VALUE || sessionId >= 0) {
            synchronized (fakeSessionIdGen) {
                int tSessionId = fakeSessionIdGen.get();
                if (tSessionId == Integer.MIN_VALUE || tSessionId >= 0) {
                    fakeSessionIdGen.set(-1);
                    sessionId = -1;
                } else {
                    sessionId = fakeSessionIdGen.decrementAndGet();
                }
            }
        }
        return sessionId;
    }

}
