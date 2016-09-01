package com.hank.corelib.tools.task;

/**
 * 在IO线程中执行的任务
 * Created by hank on 2016/8/31.
 */
public abstract class IOTask<T> {
    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }


    public IOTask(T t) {
        setT(t);
    }


    public abstract void doInIOThread();
}

