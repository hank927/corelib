package com.hank.corelib.http;

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
    private static final String DEFAULT_CACHE_FOLDER_NAME = "httpcache";

    public static final String BASE_URL = "https://api.douban.com/v2/movie/";
    private static Retrofit retrofit;
    private static OkHttpClient okHttpClient;
    private static Converter.Factory converterFactory;
    private static CallAdapter.Factory callAdapterFactory;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(getOkHttpClient())
                    .addConverterFactory(getConverterFactory())
                    .addCallAdapterFactory(getCallAdapterFactory())
                    .baseUrl(BASE_URL)
                    .build();
        }

        return retrofit;
    }

    public static Retrofit addInterceptor(Interceptor... interceptors){
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


    public static CallAdapter.Factory getCallAdapterFactory() {
        if(callAdapterFactory==null){
            callAdapterFactory = RxJavaCallAdapterFactory.create();
        }
        return callAdapterFactory;
    }

    public static Converter.Factory getConverterFactory() {
        if(converterFactory==null){
            converterFactory = GsonConverterFactory.create();
        }
        return converterFactory;
    }

    public static OkHttpClient getOkHttpClient() {
        if(okHttpClient==null){
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

//    /**
//     * 设置是否缓存
//     * @param isEnable
//     * @return
//     */
//    public static OkHttpClient enableCache(boolean isEnable){
//        return enableCache(isEnable,null);
//    }
//
//    /**
//     *
//     * @param isEnable
//     * @param cacheDirectory
//     * @return
//     */
//    public static OkHttpClient enableCache(boolean isEnable, File cacheDirectory){
//        enableCache = isEnable;
//        if(enableCache){
//            if(null==cacheDirectory){
//                cacheDirectory = new File(SDCardUtils.getDiskCacheDir(App.getApplication()),DEFAULT_CACHE_FOLDER_NAME);
//            }
//            if(!cacheDirectory.exists()){
//                cacheDirectory.mkdirs();
//            }
//            Cache cache = new Cache(cacheDirectory, DEFAULT_MAX_CACHE_SIZE);
//            //
//            OkHttpClient.Builder builder = createBuilder();
//
//            builder.addInterceptor(new CacheInterceptor())
//                    .addNetworkInterceptor(new CacheInterceptor())
//                    //
//                    .cache(cache);
//            okHttpClient = builder.build();
//            retrofit = null;
//        }else {
//
//            OkHttpClient.Builder builder = createBuilder();
//            okHttpClient = builder.build();
//            retrofit = null;
//        }
//        return getOkHttpClient();
//    }

    /**
     *
     * @return Builder
     */
    private static OkHttpClient.Builder createBuilder(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        List<Interceptor> lists = getOkHttpClient().interceptors();
        if(!CollectionUtils.isEmpty(lists)){
            for(Interceptor interceptor:lists){
//                if(interceptor instanceof CacheInterceptor && !enableCache){
//                    Logger.d("change to no cache");
//                }else
                builder.addInterceptor(interceptor);
            }
        }
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        return builder;
    }

    public static <T> T create(Class<T> clazz){
        return getRetrofit().create(clazz);
    }

}
