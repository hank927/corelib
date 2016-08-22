package com.hank.corelib.http;

import android.content.Context;
import android.text.TextUtils;

import com.hank.corelib.http.interceptor.CacheInterceptor;
import com.hank.corelib.logger.Logger;
import com.hank.corelib.util.CollectionUtils;
import com.hank.corelib.util.SDCardUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
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

    private static boolean enableCache;//

    private static final int DEFAULT_TIMEOUT = 5;
    private static final long DEFAULT_MAX_CACHE_SIZE = 20*1024*1024;
    private static final String DEFAULT_CACHE_FOLDER_NAME = "AppCache";

    private String baseUrl = "";
    private Retrofit retrofit;
    private OkHttpClient okHttpClient;
    private Converter.Factory converterFactory;
    private CallAdapter.Factory callAdapterFactory;

    private Context context;

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
        return this;
    }

    public AppClient baseUrl(String baseUrl){
        this.baseUrl = baseUrl;
        return this;
    }

    public Retrofit getRetrofit() {
        if(TextUtils.isEmpty(baseUrl)){
            throw new IllegalArgumentException("baseurl is null, it's must be set not null before start a http");
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
                cacheDirectory = new File(SDCardUtils.getDiskCacheDir(context),DEFAULT_CACHE_FOLDER_NAME);
            }
            if(!cacheDirectory.exists()){
                cacheDirectory.mkdirs();
            }
            Cache cache = new Cache(cacheDirectory, DEFAULT_MAX_CACHE_SIZE);
            //
            OkHttpClient.Builder builder = createBuilder();

            builder.addInterceptor(new CacheInterceptor(context))
                    .addNetworkInterceptor(new CacheInterceptor(context))
                    //
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
