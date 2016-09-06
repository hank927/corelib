package com.hank.corelib.http.entity;

/**
 * Created by hank on 2016/9/6.
 */
public class Result<T> {
    public String msg;
    public int respCode;
    public boolean success;
    public T data;

    @Override
    public String toString() {
        return "Result{" +
                "msg='" + msg + '\'' +
                ", respCode=" + respCode +
                ", success=" + success +
                ", data=" + data +
                '}';
    }
}
