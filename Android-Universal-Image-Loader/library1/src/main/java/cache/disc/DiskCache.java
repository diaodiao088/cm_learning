package cache.disc;

import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import utils.IoUtils;

/**
 * Created by zhangdan on 2017/9/18.
 *
 * comments:
 */

public interface DiskCache {

    File getDirectory();

    File get(String imageUri);

    boolean save(String imageUri , InputStream imageStream , IoUtils.CopyListener copyListener) throws IOException;

    boolean save(String imageUri , Bitmap bitmap) throws IOException;

    boolean remove(String imageUri);

    void close();

    void clear();

}
