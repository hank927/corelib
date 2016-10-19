package com.hank.corelib.http;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hank on 16/3/14.
 * 线程转换器
 */
public class SchedulerTransform<T> implements Observable.Transformer<T, T> {

    private static final String TAG = "SchedulerTransform" ;

    @Override
    public Observable<T> call(Observable<T> tObservable) {
        return tObservable
                       .subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .unsubscribeOn(Schedulers.io());
    }
}
