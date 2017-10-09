package core.imageaware;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import core.assist.ViewScaleType;

/**
 * Created by zhangdan on 2017/9/28.
 * comments:
 */

public interface ImageAware {

    int getWidth();

    int getHeight();

    ViewScaleType getScaleType();

    View getWrapperdView();

    boolean isCollected();

    int getId();

    boolean setImageDrawable(Drawable drawable);

    boolean setImageBitmap(Bitmap bitmap);

}
