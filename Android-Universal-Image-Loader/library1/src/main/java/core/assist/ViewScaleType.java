package core.assist;

import android.widget.ImageView;

/**
 * Created by zhangdan on 2017/9/28.
 * comments:
 */

public enum ViewScaleType {

    FIT_INSIDE,

    CROP;

    public static ViewScaleType fromImageView(ImageView imageView) {
        switch (imageView.getScaleType()) {
            case FIT_CENTER:
            case FIT_XY:
            case FIT_START:
            case FIT_END:
            case CENTER_INSIDE:
                return FIT_INSIDE;
            case MATRIX:
            case CENTER:
            case CENTER_CROP:
            default:
                return CROP;
        }
    }


}
