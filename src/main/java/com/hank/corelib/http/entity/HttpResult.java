package com.hank.corelib.http.entity;

/**
 * Created by Hank on 16/3/5.
 */
public final class HttpResult<T> {

    public int errorCode;

    public String message;

    public boolean success;

    public Result<T> result;

    @Override
    public String toString() {
        return "HttpResult{" +
                "errorCode=" + errorCode +
                ", message='" + message + '\'' +
                ", success=" + success +
                ", result=" + result +
                '}';
    }
}
