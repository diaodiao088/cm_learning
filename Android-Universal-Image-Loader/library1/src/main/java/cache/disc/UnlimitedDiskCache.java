package cache.disc;

import java.io.File;

import cache.disc.name.FileNameGenerator;

/**
 * Created by zhangdan on 2017/9/22.
 * comments:
 */

public class UnlimitedDiskCache extends  BaseDiskCache {

    public UnlimitedDiskCache(File cacheDir) {
        super(cacheDir);
    }

    public UnlimitedDiskCache(File cache, File reserveCacheDir, FileNameGenerator fileNameGenerator) {
        super(cache, reserveCacheDir, fileNameGenerator);
    }

    public UnlimitedDiskCache(File cacheDir, File reserveCacheDir) {
        super(cacheDir, reserveCacheDir);
    }
}
