package com.hank.corelib.exception;

/**
 * Created by Hank on 16/8/28.
 */
public class EmptyException extends NullPointerException {
    public EmptyException() {
        super();
    }

    public EmptyException(String detailMessage) {
        super(detailMessage);
    }

}
