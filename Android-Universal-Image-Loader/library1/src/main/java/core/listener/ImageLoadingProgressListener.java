package core.listener;

import android.view.View;

/**
 * Created by zhangdan on 2017/9/30.
 * comments:
 */

public interface ImageLoadingProgressListener
{
    void onProgressUpdate(String imageUri, View view, int current, int total);
}
