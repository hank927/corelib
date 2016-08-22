package com.hank.corelib.util;

import android.content.Context;
import android.os.Environment;

/**
 * Created by hank on 2016/8/18.
 */
public class SDCardUtils {
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
}
