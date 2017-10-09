package core.display;

import android.graphics.Bitmap;

import core.assist.LoadedFrom;
import core.imageaware.ImageAware;

/**
 * Created by zhangdan on 2017/9/29.
 *
 * comments:
 */
public class SimpleBitmapDisplayer implements BitmapDisplayer {

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        imageAware.setImageBitmap(bitmap);
    }
}
