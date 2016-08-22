package com.hank.corelib.subscribers;

/**
 * Created by Hank on 16/3/10.
 */
public interface SubscriberOnNextListener<T> {
    void onNext(T t);
}
