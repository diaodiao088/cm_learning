package cache.disc;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cache.disc.name.FileNameGenerator;
import utils.IoUtils;

/**
 * Created by zhangdan on 2017/9/19.
 *
 * comments:
 */

public class BaseDiskCache implements DiskCache {

    public static final int DEFAULT_BUFFER_SIZE = 32 * 1024;

    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;

    public static final int DEFAULT_COMPRESS_QUALITY = 100;

    private static final String ERROR_ARG_NULL = " argument must be not null";
    private static final String TEMP_IMAGE_POSTFIX = ".tmp";

    protected final File cacheDir;
    protected final File reserveCacheDir; // ???

    protected final FileNameGenerator fileNameGenerator;

    protected int bufferSize = DEFAULT_BUFFER_SIZE;

    protected Bitmap.CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;


    public BaseDiskCache(File cacheDir) {
        this(cacheDir, null);
    }

    public BaseDiskCache(File cacheDir, File reserveCacheDir) {
        this(cacheDir, reserveCacheDir, null);
    }

    public BaseDiskCache(File cache, File reserveCacheDir, FileNameGenerator fileNameGenerator) {
        this.cacheDir = cache;
        this.reserveCacheDir = reserveCacheDir;
        this.fileNameGenerator = fileNameGenerator;
    }

    @Override
    public File getDirectory() {
        return cacheDir;
    }

    @Override
    public File get(String imageUri) {
        return getFile(imageUri);
    }

    public File getFile(String imageUri) {
        String fileName = fileNameGenerator.generate(imageUri);
        File dir = cacheDir;
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            if (reserveCacheDir != null && (reserveCacheDir.exists() || reserveCacheDir.mkdirs())) {
                dir = reserveCacheDir;
            }
        }
        return new File(dir, fileName);
    }

    @Override
    public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener copyListener) throws IOException {

        File imageFile = getFile(imageUri);
        File tmpFile = new File(imageFile.getAbsolutePath() + TEMP_IMAGE_POSTFIX);
        boolean loaded = false;
        try{
            OutputStream os = new BufferedOutputStream(new FileOutputStream(tmpFile));
            try{
                loaded = IoUtils.copyFile(os , imageStream , copyListener , bufferSize);
            }finally {
                IoUtils.closeSiliently(os);
            }
        }finally{
            if (loaded && !tmpFile.renameTo(imageFile)){
                loaded = false;
            }
            if (!loaded){
                tmpFile.delete();
            }
        }
        return loaded;
    }

    @Override
    public boolean save(String imageUri, Bitmap bitmap) throws IOException {

        File imageFile = getFile(imageUri);
        File tmpFile = new File(imageFile.getAbsolutePath() + TEMP_IMAGE_POSTFIX);
        OutputStream os = new BufferedOutputStream(new FileOutputStream(tmpFile));
        boolean savedSuccessfully = false;
        try{
            savedSuccessfully = bitmap.compress(compressFormat , DEFAULT_COMPRESS_QUALITY , os);
        }catch (Exception e) {
            IoUtils.closeSiliently(os);
            if (savedSuccessfully && !tmpFile.renameTo(imageFile)){
                savedSuccessfully = false;
            }

            if (!savedSuccessfully){
                tmpFile.delete();
            }
        }
        if (bitmap != null){
            bitmap.recycle();
        }
        return savedSuccessfully;
    }

    @Override
    public boolean remove(String imageUri) {
        return getFile(imageUri).delete();
    }

    @Override
    public void close() {
        // todo nothing
    }

    @Override
    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files != null){
            for (File file: files) {
                file.delete();
            }
        }
    }
}
