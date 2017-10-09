package cache.disc;

import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cache.disc.name.FileNameGenerator;
import utils.IoUtils;

/**
 * Created by zhangdan on 2017/9/22.
 *
 * comments:
 */
public class LimitedAgeDiskCache extends BaseDiskCache {

    private final long mMaxFileAge;
    private final Map<File, Long> loadingDates = Collections.synchronizedMap(new HashMap<File, Long>());

    public LimitedAgeDiskCache(File cacheDir) {
        this(cacheDir , null);
    }

    public LimitedAgeDiskCache(File cacheDir, File reserveCacheDir) {
        this(cacheDir, reserveCacheDir , null);
    }

    public LimitedAgeDiskCache(File cache, File reserveCacheDir, FileNameGenerator fileNameGenerator) {
        this(cache, reserveCacheDir, fileNameGenerator , 0L);
    }

    public LimitedAgeDiskCache(File cache, File reserveCacheDir, FileNameGenerator fileNameGenerator , long maxAge) {
        super(cache, reserveCacheDir, fileNameGenerator);
        this.mMaxFileAge = maxAge * 1000;
    }

    @Override
    public File get(String imageUri) {
        File file =  super.get(imageUri);
        boolean cached ;
        if (file != null && file.exists()){
            Long loadingDate = loadingDates.get(file);
            if (loadingDate == null){
                cached = true;
                loadingDate = file.lastModified();
            }else{
                cached = true;
            }

            if (System.currentTimeMillis() - loadingDate > mMaxFileAge){
                file.delete();
                loadingDates.remove(file);
            }else if (!cached){
                loadingDates.put(file , loadingDate);
            }
        }
        return file;
    }

    @Override
    public boolean remove(String imageUri) {
        loadingDates.remove(getFile(imageUri));
        return super.remove(imageUri);
    }

    @Override
    public boolean save(String imageUri, Bitmap bitmap) throws IOException {
        boolean saved =  super.save(imageUri, bitmap);
        rememberUsage(imageUri);
        return saved;
    }

    @Override
    public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener copyListener) throws IOException {
        boolean saved =  super.save(imageUri, imageStream, copyListener);
        rememberUsage(imageUri);
        return saved;
    }

    @Override
    public void clear() {
        super.clear();
        loadingDates.clear();
    }

    private void rememberUsage(String imageUri){
        File file = getFile(imageUri);
        file.setLastModified(System.currentTimeMillis());
        loadingDates.put(file ,System.currentTimeMillis());

    }
}
