package com.hank.corelib.tools.task;

import rx.Observable;

/**
 * Created by hank on 2016/8/31.
 */
public abstract class TaskOnSubscribe<C> implements Observable.OnSubscribe<C> {
    private C c;

    public TaskOnSubscribe(C c) {
        setT(c);
    }

    public C getT() {
        return c;
    }

    public void setT(C c) {
        this.c = c;
    }


}