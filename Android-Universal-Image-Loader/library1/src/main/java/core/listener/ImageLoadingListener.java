package core.listener;

import android.graphics.Bitmap;
import android.view.View;

import core.assist.FailReason;

/**
 * Created by zhangdan on 2017/9/16.
 *
 * comments:
 */

public interface ImageLoadingListener {

    void onLoadingStarted(String imageUri ,View view);

    void onLoadingFailed(String imageUri , View view , FailReason failReason);

    void onLoadingFinished(String imageUri , View view ,Bitmap loadedImage);

    void onLoadingCanceled(String imageUri , View view);

}
