package com.hank.corelib.http;



import com.hank.corelib.http.entity.HttpResult;

import rx.functions.Func1;

/**
 * Created by hank on 2016/8/8.
 */
public class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {

    @Override
    public T call(HttpResult<T> httpResult) {
        if (httpResult.getCount() == 0) {
            throw new ApiException(100);
        }
        return httpResult.getSubjects();
    }
}

