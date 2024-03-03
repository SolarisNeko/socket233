package com.neko233.socket233.core.bytes.exception;


/**
 * @author LuoHaoJun on 2023-06-14
 **/
public class BytesException extends RuntimeException {

    public BytesException(String message) {
        super(message);
    }

    public BytesException(Throwable cause, String message) {
        super(message, cause);
    }

}
