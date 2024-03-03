package com.neko233.socket233.core.utils;

/**
 * 时间花费工具
 *
 * @author LuoHaoJun on 2023-06-19
 **/
public class TimePieceUtils {

    /**
     * 执行代码, 返回他们执行的时间
     *
     * @param runnable 执行的代码
     * @return 耗时 timeMs
     * @throws Throwable 异常
     */
    public static long executeAndReturnSpendMs(Runnable runnable) throws Throwable {
        long startMs = System.currentTimeMillis();
        runnable.run();
        long endMs = System.currentTimeMillis();
        return endMs - startMs;
    }

}
