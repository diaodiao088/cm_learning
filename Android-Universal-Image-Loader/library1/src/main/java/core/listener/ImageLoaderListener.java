package core.listener;

import android.graphics.Bitmap;
import android.view.View;

import core.assist.FailReason;

/**
 * Created by zhangdan on 2017/9/30.
 * comments:
 */

public interface ImageLoaderListener {

    void onLoadingStart(String imageUri , View view);

    void onLoadingFailed(String iamgeUri , View view , FailReason failReason);

    void onLoadingFinished(String iamgeUri , View view, Bitmap bitmap);

    void onLoadingCancled(String imageUri , View view);

}
