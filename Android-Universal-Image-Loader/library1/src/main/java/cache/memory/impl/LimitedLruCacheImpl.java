package cache.memory.impl;

import android.graphics.Bitmap;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import cache.memory.LimitedMemoryCache;

/**
 * Created by zhangdan on 2017/9/18.
 *
 * comments:
 */

public class LimitedLruCacheImpl extends LimitedMemoryCache {

    public static final int INITIAL_CAPACITY = 10;
    public static final float LOAD_FACTOR = 1.1f;

    private final Map<String , Bitmap> lruCache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(INITIAL_CAPACITY , LOAD_FACTOR , true));

    public LimitedLruCacheImpl(int sizeLimit) {
        super(sizeLimit);
    }

    @Override
    protected int getSize(Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public boolean put(String key, Bitmap value) {
        if (super.put(key , value)){
            lruCache.put(key , value);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Bitmap remove(String key) {
        lruCache.remove(key);
        return super.remove(key);
    }

    @Override
    public Bitmap get(String key) {
        return super.get(key);
    }

    @Override
    protected Bitmap removeNext() {
        Bitmap mostLongUsedValue = null;
        synchronized (lruCache){
            Iterator iterator =   lruCache.entrySet().iterator();
            if (iterator.hasNext()){
                Map.Entry<String , Bitmap> entry = (Map.Entry<String, Bitmap>) iterator.next();
                mostLongUsedValue = entry.getValue();
                iterator.remove();
            }
        }
        return mostLongUsedValue;
    }

    @Override
    public Reference<Bitmap> createRef(Bitmap bmp) {
        return new WeakReference<Bitmap>(bmp);
    }
}
