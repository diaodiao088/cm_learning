package cache.memory;

import android.graphics.Bitmap;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by zhangdan on 2017/9/16.
 *
 * comments:
 */

public abstract class BaseMemoryCache implements MemoryCache {

    private final Map<String , Reference<Bitmap>> softMap = Collections.synchronizedMap(new HashMap<String, Reference<Bitmap>>());

    @Override
    public boolean put(String key, Bitmap value) {
        softMap.put(key , createRef(value));
        return true;
    }

    @Override
    public Bitmap get(String key) {

        Bitmap result = null;
        Reference<Bitmap> bmpRef = softMap.get(key);

        if (bmpRef != null){
            result = bmpRef.get();
        }
        return result;
    }

    @Override
    public Bitmap remove(String key) {
        Reference<Bitmap> bmp = softMap.remove(key);
        return bmp == null ? null : bmp.get();
    }

    @Override
    public Collection<String> keys() {

        synchronized (softMap){
            return new HashSet<String>(softMap.keySet());
        }
    }

    @Override
    public void clear() {
        softMap.clear();
    }

    public abstract Reference<Bitmap> createRef(Bitmap bmp);

}
