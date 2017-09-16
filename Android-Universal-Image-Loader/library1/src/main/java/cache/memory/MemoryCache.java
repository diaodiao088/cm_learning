package cache.memory;

import android.graphics.Bitmap;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by zhangdan on 2017/9/16.
 *
 * comments:
 */

public interface MemoryCache  {

    boolean put(String key , Bitmap value);

    Bitmap get(String key);

    Bitmap remove(String key);

    Collection<String> keys();

    void clear();
}
