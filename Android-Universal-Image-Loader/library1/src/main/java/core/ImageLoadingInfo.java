package core;

import java.util.concurrent.locks.ReentrantLock;

import core.assist.DisplayImageOptions;
import core.assist.ImageSize;
import core.imageaware.ImageAware;
import core.listener.ImageLoadingListener;
import core.listener.ImageLoadingProgressListener;

/**
 * Created by zhangdan on 2017/9/30.
 * comments:
 */

public class ImageLoadingInfo {

    final String uri;
    final String memoryCacheKey;
    final ImageAware imageAware;
    final ImageSize targetSize;
    final DisplayImageOptions options;
    final ImageLoadingListener listener;
    final ImageLoadingProgressListener progressListener;
    final ReentrantLock loadFromUriLock;

    public ImageLoadingInfo(String uri, ImageAware imageAware, ImageSize targetSize, String memoryCacheKey,
                            DisplayImageOptions options, ImageLoadingListener listener,
                            ImageLoadingProgressListener progressListener, ReentrantLock loadFromUriLock) {
        this.uri = uri;
        this.imageAware = imageAware;
        this.targetSize = targetSize;
        this.options = options;
        this.listener = listener;
        this.progressListener = progressListener;
        this.loadFromUriLock = loadFromUriLock;
        this.memoryCacheKey = memoryCacheKey;
    }


}
