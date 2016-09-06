package com.hank.corelib.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.hank.corelib.http.interceptor.CacheInterceptor;
import com.hank.corelib.logger.Logger;
import com.hank.corelib.util.CollectionUtils;
import com.hank.corelib.util.FileUtil;
import com.hank.corelib.util.SDCardUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @page Created by Hank on 2016/4/6.
 * you can use this to build a retrofit easily
 */
public class AppClient {

    private static final int DEFAULT_TIMEOUT = 5;
    private static final long DEFAULT_MAX_CACHE_SIZE = 20*1024*1024;
    private static final String DEFAULT_CACHE_FOLDER_NAME = "AppCache";

    private Context context;
    private String baseUrl = "";
    private Retrofit retrofit;
    private boolean enableCache;//
    private OkHttpClient okHttpClient;
    private Converter.Factory converterFactory;
    private CallAdapter.Factory callAdapterFactory;

    private ArrayMap<String, Interceptor> interceptors;

    private AppClient() {
    }

    public static AppClient getInstance(){
        return StaticInnerHolder.INSTANCE;
    }

    private static class StaticInnerHolder{
        private static AppClient INSTANCE = new AppClient();
    }

    public AppClient init(Context context){
        this.context = context;
        interceptors = new ArrayMap<>();
        return this;
    }

    public AppClient baseUrl(String baseUrl){
        this.baseUrl = baseUrl;
        return this;
    }

    public Retrofit getRetrofit() {
        if(TextUtils.isEmpty(baseUrl)){
            throw new IllegalArgumentException("baseurl must be set not null before start a http");
        }
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(getOkHttpClient())
                    .addConverterFactory(getConverterFactory())
                    .addCallAdapterFactory(getCallAdapterFactory())
                    .baseUrl(baseUrl)
                    .build();
        }

        return retrofit;
    }

    public Retrofit addInterceptor(Interceptor... interceptors){
        OkHttpClient.Builder builder = createBuilder();
        if(!CollectionUtils.isEmpty(interceptors)){
            for(Interceptor interceptor:interceptors){
                builder.addInterceptor(interceptor);
            }
        }

        okHttpClient = builder.build();
        retrofit = null;
        return getRetrofit();
    }

    public Retrofit addHeader(final String key,final String value){
        return addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .removeHeader(key)
                        .addHeader(key, value)
                        .build();
                return chain.proceed(request);
            }
        });
    }

    public Retrofit removeHeader(final String key){
        return addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .removeHeader(key)
                        .build();
                return chain.proceed(request);
            }
        });
    }

    public Retrofit addHeader(final ArrayMap<String, String> headers){
        return addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
               Request.Builder builder = chain.request()
                        .newBuilder();
                if(!CollectionUtils.isEmpty(headers)){
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        builder.removeHeader(entry.getKey()).addHeader(entry.getKey(),entry.getValue());
                    }
                }
                Request request = builder.build();
                return chain.proceed(request);
            }
        });
    }

    public CallAdapter.Factory getCallAdapterFactory() {
        if(callAdapterFactory==null){
            callAdapterFactory = RxJavaCallAdapterFactory.create();
        }
        return callAdapterFactory;
    }

    public Converter.Factory getConverterFactory() {
        if(converterFactory==null){
            converterFactory = GsonConverterFactory.create();
        }
        return converterFactory;
    }

    public OkHttpClient getOkHttpClient() {
        if(okHttpClient==null){
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    /**
     * 设置是否缓存
     * @param isEnable
     * @return
     */
    public OkHttpClient enableCache(boolean isEnable){
        return enableCache(isEnable,null);
    }

    /**
     *
     * @param isEnable  是否要开启缓存
     * @param cacheDirectory  缓存路径
     * @return
     */
    public OkHttpClient enableCache(boolean isEnable, File cacheDirectory){
        enableCache = isEnable;
        if(enableCache){
            if(null==cacheDirectory){
                cacheDirectory = new File(FileUtil.getDiskCacheDir(context),DEFAULT_CACHE_FOLDER_NAME);
            }
            if(!cacheDirectory.exists()){
                cacheDirectory.mkdirs();
            }
            Cache cache = new Cache(cacheDirectory, DEFAULT_MAX_CACHE_SIZE);
            //
            OkHttpClient.Builder builder = createBuilder();

            builder.addInterceptor(new CacheInterceptor(context))
                    .addNetworkInterceptor(new CacheInterceptor(context))
                    .cache(cache);
            okHttpClient = builder.build();
            retrofit = null;
        }else {

            OkHttpClient.Builder builder = createBuilder();
            okHttpClient = builder.build();
            retrofit = null;
        }
        return getOkHttpClient();
    }

    /**
     *
     * @return Builder
     */
    private OkHttpClient.Builder createBuilder(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        List<Interceptor> lists = getOkHttpClient().interceptors();
        if(!CollectionUtils.isEmpty(lists)){
            for(Interceptor interceptor:lists){
                if(interceptor instanceof CacheInterceptor && !enableCache){
                    Logger.d("change to no cache");
                }else
                builder.addInterceptor(interceptor);
            }
        }
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        return builder;
    }

    public <T> T create(Class<T> clazz){
        return getRetrofit().create(clazz);
    }

}
