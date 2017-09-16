package cache.memory;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangdan on 2017/9/16.
 * comments:
 */

public abstract class LimitedMemoryCache extends BaseMemoryCache {

    public static final int MAX_NORMAL_CACHE_SIZE_IN_MB = 16;

    public static final int MAX_NORMAL_CACHE_SIZE_IN_KB = 16 * 1024 * 1024;
    private final int sizeLimit;

    private final AtomicInteger cacheSize;

    private final List<Bitmap> hardCache = Collections.synchronizedList(new LinkedList<Bitmap>());


    public LimitedMemoryCache(int sizeLimit){
        this.sizeLimit = sizeLimit;
        cacheSize = new AtomicInteger();
    }

    @Override
    public boolean put(String key, Bitmap value) {

        boolean putSuccessfully = false;

        int valueSize = getSize(value);
        int sizeLimit = getSizeLimit();

        int curCacheSize = cacheSize.get();

        if (valueSize < sizeLimit){
            while(curCacheSize + valueSize > sizeLimit){
                Bitmap removedValue = removeNext();
                if (removedValue != null){
                    curCacheSize = cacheSize.addAndGet( -getSize(removedValue));
                }
            }
            hardCache.add(value);
            cacheSize.addAndGet(valueSize);
            putSuccessfully = true;
        }

        super.put(key, value);

        return putSuccessfully;
    }

    @Override
    public Bitmap remove(String key) {

        Bitmap value = super.get(key);

        if (value != null){
            if (hardCache.remove(value)){
                cacheSize.addAndGet(-getSize(value));
            }
        }

        return super.remove(key);
    }

    @Override
    public void clear() {
        super.clear();
    }

    protected int getSizeLimit(){
        return sizeLimit;
    }

    protected abstract int getSize(Bitmap value);

    protected abstract Bitmap removeNext();
}
