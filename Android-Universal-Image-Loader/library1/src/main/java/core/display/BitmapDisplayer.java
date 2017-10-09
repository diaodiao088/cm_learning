package core.display;

import android.graphics.Bitmap;

import core.assist.LoadedFrom;
import core.imageaware.ImageAware;

/**
 * Created by zhangdan on 2017/9/28.
 * comments:
 */

public interface BitmapDisplayer {

    void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom);

}
