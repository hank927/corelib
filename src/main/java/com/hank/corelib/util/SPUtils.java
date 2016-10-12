package com.hank.corelib.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Process;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author:hank
 */
public class SPUtils
{  
    /** 
     * 保存在手机里面的文件名 
     */  
    public static final String FILE_NAME = "share_data";


    private SPUtils() {
        throw new SharedPreferencesException("Stub!");
    }


    /** 
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法 
     *  
     * @param context 
     * @param key 
     * @param value
     */  
    public static void put(Context context, String key, Object value)
    {
        checkNotNull(context, "Context can not be null!");
        checkNotEmpty(key, "SharedPreferences key can not be empty!");
        checkNotNull(value, "SharedPreferences value can not be null!");
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);  
        SharedPreferences.Editor editor = sp.edit();  
  
        if (value instanceof String)
        {  
            editor.putString(key, (String) value);
        } else if (value instanceof Integer)
        {  
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean)
        {  
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float)
        {  
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long)
        {  
            editor.putLong(key, (Long) value);
        } else  
        {  
            editor.putString(key, value.toString());
        }

        SharedPreferencesEditorCompat.apply(editor);
    }  
  
    /** 
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值 
     *  
     * @param context 
     * @param key 
     * @param defaultObject 
     * @return 
     */  
    public static Object get(Context context, String key, Object defaultObject)  
    {
        checkNotNull(context, "Context can not be null!");
        checkNotEmpty(key, "SharedPreferences key can not be empty!");
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);  
  
        if (defaultObject instanceof String)  
        {  
            return sp.getString(key, (String) defaultObject);  
        } else if (defaultObject instanceof Integer)  
        {  
            return sp.getInt(key, (Integer) defaultObject);  
        } else if (defaultObject instanceof Boolean)  
        {  
            return sp.getBoolean(key, (Boolean) defaultObject);  
        } else if (defaultObject instanceof Float)  
        {  
            return sp.getFloat(key, (Float) defaultObject);  
        } else if (defaultObject instanceof Long)  
        {  
            return sp.getLong(key, (Long) defaultObject);  
        }  
  
        return null;  
    }  
  
    /** 
     * 移除某个key值已经对应的值 
     * @param context 
     * @param key 
     */  
    public static void remove(Context context, String key)  
    {
        checkNotNull(context, "Context can not be null!");
        checkNotEmpty(key, "SharedPreferences key can not be empty!");
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);  
        SharedPreferences.Editor editor = sp.edit();  
        editor.remove(key);
        SharedPreferencesEditorCompat.apply(editor);
    }  
  
    /** 
     * 清除所有数据 
     * @param context 
     */  
    public static void clear(Context context)  
    {
        checkNotNull(context, "Context can not be null!");
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);  
        SharedPreferences.Editor editor = sp.edit();  
        editor.clear();
        SharedPreferencesEditorCompat.apply(editor);
    }  
  
    /** 
     * 查询某个key是否已经存在 
     * @param context 
     * @param key 
     * @return 
     */  
    public static boolean contains(Context context, String key)  
    {
        checkNotNull(context, "Context can not be null!");
        checkNotEmpty(key, "SharedPreferences key can not be empty!");
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);  
        return sp.contains(key);  
    }  
  
    /** 
     * 返回所有的键值对 
     *  
     * @param context 
     * @return 
     */  
    public static Map<String, ?> getAll(Context context)
    {
        checkNotNull(context, "Context can not be null!");
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);  
        return sp.getAll();  
    }

    private static <T> void checkNotNull(T t, String message) {
        if (t == null)
            throw new SharedPreferencesException(String.valueOf(message));
    }

    private static void checkNotEmpty(String t, String message) {
        if (TextUtils.isEmpty(t))
            throw new SharedPreferencesException(String.valueOf(message));
    }

    private static final class SharedPreferencesException extends RuntimeException {
        public SharedPreferencesException(String message, Throwable cause) {
            super(message, cause);
        }

        public SharedPreferencesException(String message) {
            super(message);
        }
    }

    private static final class SharedPreferencesEditorCompat{
        private static final ExecutorService SINGLE_THREAD_POOL;

        static {
            SINGLE_THREAD_POOL = Executors.newFixedThreadPool(1, new SharedPreferencesThreadFactory());
        }

        static void apply(final SharedPreferences.Editor editor) {
            try {
                editor.apply();
            } catch (Throwable e) {
                SINGLE_THREAD_POOL.submit(new Runnable() {
                    @Override
                    public void run() {
                        editor.commit();
                    }
                });
            }
        }
    }
    private static final class SharedPreferencesThreadFactory implements ThreadFactory {

        private static final String THREAD_NAME = "SharedPreferencesThread";

        @Override
        public Thread newThread(@NonNull final Runnable r) {
            Runnable wrapper = new Runnable() {
                @Override
                public void run() {
                    try {
                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    r.run();
                }
            };
            Thread thread = new Thread(wrapper, THREAD_NAME);
            if (thread.isDaemon())
                thread.setDaemon(false);
            return thread;
        }
    }
}  