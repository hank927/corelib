package com.hank.corelib.http;



import com.hank.corelib.http.entity.HttpResult;
import com.hank.corelib.http.entity.Result;
import com.hank.corelib.logger.Logger;

import rx.functions.Func1;

/**
 * Created by hank on 2016/8/8.
 */
public class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {

    @Override
    public T call(HttpResult<T> httpResult) {
        Result<T> result = httpResult.result;
        Logger.d(result.toString());
        if (!result.success) {
            throw new ApiException(result.respCode);
        }
        return result.data;
    }
}

