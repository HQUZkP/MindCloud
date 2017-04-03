package com.kaipingzhou.mindcloud.utils;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Created by 周开平 on 2017/3/27 20:54.
 * qq 275557625@qq.com
 * 作用：
 */

public class MemoryCache {
    private static final HashMap<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();

    Bitmap get(final String id) {
        if (!cache.containsKey(id)) return null;
        SoftReference<Bitmap> ref = cache.get(id);
        return ref.get();
    }

    void put(final String id, final Bitmap bitmap) {
        cache.put(id, new SoftReference<Bitmap>(bitmap));
    }
}
