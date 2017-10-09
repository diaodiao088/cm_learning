package core.listener;

import android.graphics.Bitmap;
import android.view.View;

import core.assist.FailReason;

/**
 * Created by zhangdan on 2017/9/30.
 * comments:
 */

public class SimpleLoadingListenerImpl implements ImageLoadingListener {
    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingFinished(String imageUri, View view, Bitmap loadedImage) {

    }

    @Override
    public void onLoadingCanceled(String imageUri, View view) {

    }
}
