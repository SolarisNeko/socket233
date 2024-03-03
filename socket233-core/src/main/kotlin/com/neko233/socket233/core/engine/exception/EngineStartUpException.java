package com.neko233.socket233.core.engine.exception;


/**
 * @author LuoHaoJun on 2023-06-19
 **/
public class EngineStartUpException extends RuntimeException {

    public EngineStartUpException(String message, Object... args) {
        super(message);
    }

    public EngineStartUpException(Throwable cause, String message) {
        super(message, cause);
    }

}
