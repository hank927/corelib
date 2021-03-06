package com.hank.corelib.proxy;

import android.content.Context;
import android.util.ArrayMap;

import com.hank.corelib.logger.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SingleInstanceManager {

    public interface SingleInstanceBase {

        void init(Context context);

    }

    private static final String TAG = "SingleInstanceManager";

    private static SingleInstanceManager gSingleInstanceManager;

    private static Object obj = new Object();

    private Context mContext;

    private ArrayMap<Class, SingleInstanceBase> mSingleInstanceCache;

    public synchronized static final <T> T getSingleInstanceByClass(Class<T> classType) {
        if (classType == null) {
            throw new IllegalArgumentException("argument is null");
        }

        SingleInstanceBase ret = SingleInstanceManager.getInstance().getInstance(classType);
        if (ret == null) {
            ret = SingleInstanceManager.getInstance().makeNewInstance(classType);
            if (ret != null) {
                SingleInstanceManager.getInstance().putInstace(classType, ret);
            }
        }

        if (ret == null) {
            throw new IllegalArgumentException("create new instance for " + classType + " error");
        }

        return (T) ret;
    }


    public static final SingleInstanceManager getInstance() {
        if (gSingleInstanceManager == null) {
            synchronized (obj) {
                if (gSingleInstanceManager == null) {
                    gSingleInstanceManager = new SingleInstanceManager();
                }
            }
        }

        return gSingleInstanceManager;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
    }

    private SingleInstanceManager() {
        mSingleInstanceCache = new ArrayMap<Class, SingleInstanceBase>();
    }

    private SingleInstanceBase getInstance(Class classType) {
        if (mSingleInstanceCache.containsKey(classType)) {
            return mSingleInstanceCache.get(classType);
        }

        return null;
    }

    private void putInstace(Class classType, SingleInstanceBase obj) {
        if (classType != null && obj != null) {
            mSingleInstanceCache.put(classType, obj);
        }
    }

    public void clearInstance(Class classType) {
        if (classType != null) {
            mSingleInstanceCache.remove(classType);
        }
    }

    public void clearAllInstance() {
        mSingleInstanceCache.clear();
    }

    public ArrayList<SingleInstanceBase> getAllInstance() {
        ArrayList<SingleInstanceBase> ret = new ArrayList<SingleInstanceBase>();
        ret.addAll(mSingleInstanceCache.values());

        return ret;
    }

    private SingleInstanceBase makeNewInstance(Class classType) {
        if (classType == null) {
            return null;
        }

        try {

            Logger.d(TAG, "[[makeNewInstance]] class name = " + classType.getName());

            // Class classObj = Class.forName(classType.getName());
            Constructor<?> c = classType.getDeclaredConstructor();


            Logger.d(TAG, "[[makeNewInstance]] Constructor = " + c.toGenericString() + " accessible = " + c.isAccessible());


            c.setAccessible(true);


            Logger.d(TAG, "[[makeNewInstance]] Constructor changed = " + c.isAccessible());


            Object obj = c.newInstance();


            Logger.d(TAG, "[[makeNewInstance]] create obj = " + obj + " SingleInstanceBase is " + (obj instanceof SingleInstanceBase));


            if ((obj != null) && (obj instanceof SingleInstanceBase)) {
                Method m = classType.getDeclaredMethod("init", Context.class);
                if (m != null) {
                    m.setAccessible(true);
                    m.invoke(obj, mContext);
                } else {
                    throw new IllegalArgumentException("can't find init(Context context) method");
                }

                return (SingleInstanceBase) obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
