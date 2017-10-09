package cache.disc.name.ext;

import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import cache.disc.DiskCache;
import cache.disc.name.FileNameGenerator;
import utils.IoUtils;

/**
 * Created by zhangdan on 2017/9/22.
 * comments:
 */

public class LruDiskCache implements DiskCache {

    public static final int DEFAULT_BUFFER_SIZE = 32 * 1024;

    private final File reserverCacheDir;
    private final FileNameGenerator fileNameGenerator;

    public LruDiskCache(File cacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize) throws IOException {
        this(cacheDir, null, fileNameGenerator, cacheMaxSize, 0);
    }

    public LruDiskCache(File cacheDir , File reserverCacheDir , FileNameGenerator fileNameGenerator ,
                        long cacheMaxSize , long cacheMaxFileCount) throws IOException {
        this.reserverCacheDir = reserverCacheDir;
        this.fileNameGenerator = fileNameGenerator;
        initCache(cacheDir , cacheMaxSize , cacheMaxFileCount);
    }


    private void initCache(File cacheDir, long cacheMaxSize, long cacheMaxFileCount) throws  IOException {


    }


    @Override
    public File getDirectory() {
        return null;
    }

    @Override
    public File get(String imageUri) {
        return null;
    }

    @Override
    public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener copyListener) throws IOException {
        return false;
    }

    @Override
    public boolean save(String imageUri, Bitmap bitmap) throws IOException {
        return false;
    }

    @Override
    public boolean remove(String imageUri) {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public void clear() {

    }
}
