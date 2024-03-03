package com.neko233.socket233.core.engine.exceptionHandler;

/**
 * @author LuoHaoJun on 2023-06-15
 **/
public interface GlobalExceptionHandler {

    /**
     * 处理异常
     *
     * @param e 任何异常
     */
    void handleException(Throwable e);

}
