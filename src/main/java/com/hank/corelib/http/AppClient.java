package com.hank.corelib.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.SparseArray;

import com.hank.corelib.http.converter.GsonConverterFactory;
import com.hank.corelib.http.interceptor.CacheInterceptor;
import com.hank.corelib.http.interceptor.LoggerInterceptor;
import com.hank.corelib.logger.Logger;
import com.hank.corelib.util.CollectionUtils;
import com.hank.corelib.util.FileUtil;
import com.hank.corelib.util.SDCardUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
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
//    private ArrayMap<String, WeakReference<Class>> proxyClasses;

    private List<Observer> observers = new ArrayList<Observer>();
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
            okHttpClient = getOkHttpClient();
            converterFactory = getConverterFactory();
            callAdapterFactory = getCallAdapterFactory();
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(converterFactory)
                    .addCallAdapterFactory(callAdapterFactory)
                    .baseUrl(baseUrl)
                    .build();
            notifyOb();
        }

        return retrofit;
    }

    public Retrofit addInterceptor(Interceptor... interceptors){
        OkHttpClient.Builder builder = createBuilder(interceptors);
        okHttpClient = builder.build();
        retrofit = null;
        return getRetrofit();
    }

    public AppClient addHeader(final String key,final String value){
        addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .header(key, value)
                        .build();
                return chain.proceed(request);
            }
        });
        return this;
    }

    public AppClient removeHeader(final String key){
        addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .removeHeader(key)
                        .build();
                return chain.proceed(request);
            }
        });
        return this;
    }

    public AppClient addHeader(final ArrayMap<String, String> headers){
         addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
               Request.Builder builder = chain.request()
                        .newBuilder();
                if(!CollectionUtils.isEmpty(headers)){
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        builder.header(entry.getKey(),entry.getValue());
                    }
                }
                Request request = builder.build();
                return chain.proceed(request);
            }
        });
        return this;
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
    private OkHttpClient.Builder createBuilder(Interceptor... interceptors){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        List<Interceptor> lists = new ArrayList<>();
        List<Interceptor> ls = getOkHttpClient().interceptors();
        if(!CollectionUtils.isEmpty(ls))
            lists.addAll(ls);
        if(!CollectionUtils.isEmpty(interceptors)){
            for(Interceptor interceptor:interceptors)
                lists.add(interceptor);
        }
        List<Interceptor> loggerInterceptors = new ArrayList<>();
            for(Interceptor interceptor:lists){
                if(interceptor instanceof CacheInterceptor && !enableCache){
                    Logger.d("change to no cache");
                }else {
                    if(interceptor instanceof LoggerInterceptor){
                        loggerInterceptors.add( interceptor);
                    } else
                        builder.addInterceptor(interceptor);
                }
            }
        if(!CollectionUtils.isEmpty(loggerInterceptors)){
            for(Interceptor interceptor:loggerInterceptors)
                builder.addInterceptor(interceptor);
        }
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        return builder;
    }

    public <T> T create(Class<T> clazz){
        return getRetrofit().create(clazz);
    }

    public  void attach(Observer o){
        observers.add(o);
    }

    public void  detach(Observer o){
        observers.remove(o);
    }

    public void notifyOb(){
        Logger.i("notify Invoked");
        for (int i=0; i<observers.size(); i++){
            observers.get(i).refreshService();
        }
    }

}
