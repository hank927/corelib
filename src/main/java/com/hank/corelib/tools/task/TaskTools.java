package com.hank.corelib.tools.task;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Rxjava封装工具类
 * Created by hank on 2016/8/31.
 */
public class TaskTools {

    /**
     * 在ui线程中工作
     *
     * @param uiTask
     */
    public static <T> void executeUITask(UITask<T> uiTask) {

        executeUITaskDelay(uiTask, 0, TimeUnit.MILLISECONDS);
    }


    /**
     * 延时在主线程中执行任务
     *
     * @param uiTask
     * @param time
     * @param timeUnit
     * @param <T>
     */
    public static <T> void executeUITaskDelay(UITask<T> uiTask, long time, TimeUnit timeUnit) {
        Observable.just(uiTask)
                .delay(time, timeUnit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UITask<T>>() {
                    @Override
                    public void call(UITask<T> uitask) {
                        uitask.doInUIThread();
                    }
                });
    }


    /**
     * 在IO线程中执行任务
     *
     * @param <T>
     */
    public static <T> void executeIOTask(IOTask<T> ioTask) {
        executeIOTaskDelay(ioTask, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 延时在IO线程中执行任务
     *
     * @param <T>
     */
    public static <T> void executeIOTaskDelay(IOTask<T> ioTask, long time, TimeUnit timeUnit) {
        Observable.just(ioTask)
                .delay(time, timeUnit)
                .observeOn(Schedulers.io())
                .subscribe(new Action1<IOTask<T>>() {
                    @Override
                    public void call(IOTask<T> ioTask) {
                        ioTask.doInIOThread();
                    }
                });
    }


    /**
     * 执行Rx通用任务 (IO线程中执行耗时操作 执行完成调用UI线程中的方法)
     *
     * @param t
     * @param <T>
     */
    public static <T> void executeCommonTask(CommonTask<T> t) {
        executeCommonTaskDelay(t, 0, TimeUnit.MILLISECONDS);
    }


    /**
     * 延时执行Rx通用任务 (IO线程中执行耗时操作 执行完成调用UI线程中的方法)
     *
     * @param t
     * @param <T>
     */
    public static <T> void executeCommonTaskDelay(CommonTask<T> t, long time, TimeUnit timeUnit) {
        TaskOnSubscribe<CommonTask<T>> onsubscribe = new TaskOnSubscribe<CommonTask<T>>(t) {
            @Override
            public void call(Subscriber<? super CommonTask<T>> subscriber) {
                getT().doInIOThread();
                subscriber.onNext(getT());
                subscriber.onCompleted();
            }
        };
        Observable.create(onsubscribe)
                .delay(time, timeUnit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CommonTask<T>>() {
                    @Override
                        public void call(CommonTask<T> t) {
                            t.doInUIThread();
                    }
                });
    }

}
