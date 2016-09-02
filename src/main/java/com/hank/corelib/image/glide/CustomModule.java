package com.hank.corelib.image.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.module.GlideModule;
import com.hank.corelib.util.FileUtil;
import com.hank.corelib.util.SDCardUtils;

import java.io.File;

/**
 * Created by Hank on 16/6/15
 */
public class CustomModule implements GlideModule {
    @Override
    public void applyOptions(final Context context, GlideBuilder builder) {
        // Apply options to the builder here.

        builder.setDiskCache(new DiskCache.Factory() {
            @Override
            public DiskCache build() {
                // Careful: the external cache directory doesn't enforce permissions
                File cacheLocation = new File(FileUtil.getImageCacheDir(context), "glide");
                cacheLocation.mkdirs();
                //104857600 == 100M
                return DiskLruCacheWrapper.get(cacheLocation, 104857600);
            }
        });
//        builder.setDiskCache(new DiskLruCacheFactory(FileUtils.getCacheDir(), 104857600));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.

    }
}
