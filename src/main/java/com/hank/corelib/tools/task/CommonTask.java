package com.hank.corelib.tools.task;

/**
 * 通用的Rx执行任务
 * Created by hank on 2016/8/31.
 */
public abstract class CommonTask<T> {
    public CommonTask(T t) {
        setT(t);
    }

    public CommonTask() {

    }

    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public abstract void doInIOThread();

    public abstract void doInUIThread();

}

