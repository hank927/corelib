package com.hank.corelib.exception;

/**
 * Created by Hank on 16/8/28.
 * 反射过程中异常类
 */
public class ReflectException extends RuntimeException {
    public ReflectException() {
        super();
    }

    public ReflectException(String detailMessage) {
        super(detailMessage);
    }

    public ReflectException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ReflectException(Throwable throwable) {
        super(throwable);
    }
}
