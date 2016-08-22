package com.hank.corelib.http.interceptor;

import android.content.Context;

import com.hank.corelib.helper.Network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @page Created by Hank on 2016/4/6.
 */
public class CacheInterceptor implements Interceptor {

    private Context context;

    public CacheInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originalResponse = chain.proceed(request);
        if(Network.isConnected(context)) {
            //有网的时候读接口上的@Headers里的配置
            String cacheControl = request.cacheControl().toString();
            return originalResponse.newBuilder()
                    .removeHeader("Cache-Control")
                    .removeHeader("Pragma")
                    .header("Cache-Control", cacheControl)
                    .build();
        } else {
            return originalResponse.newBuilder()
                    .removeHeader("Cache-Control")
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=20")
                    .build();
        }
    }
}
