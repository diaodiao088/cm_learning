package core.display;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.renderscript.Sampler;
import android.view.View;
import android.view.animation.LinearInterpolator;

import core.assist.LoadedFrom;
import core.imageaware.ImageAware;

/**
 * Created by zhangdan on 2017/9/28.
 * comments:
 */

public class FadeInBitmapDisplayer implements BitmapDisplayer {

    private final int mDurations;

    private final boolean animFromNetWork;

    private final boolean animFromDisk;

    private final boolean animFromMemory;

    public FadeInBitmapDisplayer(int mDurations, boolean animFromNetWork, boolean animFromDisk, boolean animFromMemory) {
        this.mDurations = mDurations;
        this.animFromNetWork = animFromNetWork;
        this.animFromDisk = animFromDisk;
        this.animFromMemory = animFromMemory;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {

        imageAware.setImageBitmap(bitmap);

        if ((animFromNetWork && loadedFrom == LoadedFrom.NETWORK) ||
                (animFromDisk && loadedFrom == LoadedFrom.DISC_CACHE) ||
                (animFromMemory && loadedFrom == LoadedFrom.MEMORY_CACHE)) {
            animate(imageAware.getWrapperdView(), mDurations);
        }
    }

    private void animate(final View wrapperdView, int mDurations) {
        if (wrapperdView != null){
            ValueAnimator fadeInAnimator = ValueAnimator.ofFloat(0 , 1);
            fadeInAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    wrapperdView.setAlpha(value);
                }
            });
            fadeInAnimator.setDuration(500);
            fadeInAnimator.setInterpolator(new LinearInterpolator());
            fadeInAnimator.start();
        }
    }
}
