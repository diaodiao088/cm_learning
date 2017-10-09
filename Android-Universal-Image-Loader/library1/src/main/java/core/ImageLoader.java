package core;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ViewAnimator;

import core.assist.BitmapProcessor;
import core.assist.DisplayImageOptions;
import core.assist.ImageSize;
import core.assist.LoadedFrom;
import core.imageaware.ImageAware;
import core.listener.ImageLoadingListener;
import core.listener.ImageLoadingProgressListener;
import core.listener.SimpleLoadingListenerImpl;
import utils.ImageSizeUtils;
import utils.MemoryCacheUtils;

/**
 * Created by zhangdan on 2017/9/16.
 * <p>
 * comments:
 */

public class ImageLoader {

    public static final String TAG = ImageLoader.class.getSimpleName();

    private volatile static ImageLoader sInstance;

    private ImageLoaderConfiguration mConfiguration;

    private ImageLoaderEngine engine;

    private ImageLoadingListener defaultListener = new SimpleLoadingListenerImpl();


    public static ImageLoader getInstance() {

        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new ImageLoader();
                }
            }
        }
        return sInstance;
    }

    private ImageLoader() {

    }

    public synchronized void init(ImageLoaderConfiguration configuration) {
        if (configuration == null) {
            return;
        }
        if (mConfiguration == null) {
            engine = new ImageLoaderEngine(mConfiguration);
            this.mConfiguration = configuration;
        }
    }

    public boolean isInited() {
        return mConfiguration != null;
    }

    public void displayImage(String uri, ImageAware imageAware) {
        displayImage(uri, imageAware, null, null, null);
    }

    public void displayImage(String uri, ImageAware imageAware, ImageLoadingListener listener) {
        displayImage(uri, imageAware, null, listener, null);
    }

    public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options) {
        displayImage(uri, imageAware, options, null, null);
    }

    public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options,
                             ImageLoadingListener listener) {
        displayImage(uri, imageAware, options, listener, null);
    }

    public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options,
                             ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
        displayImage(uri, imageAware, options, null, listener, progressListener);
    }

    private void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options, ImageSize targetSize,
                              ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
        if (listener == null) {
            listener = defaultListener;
        }

        // consider uri is empty
        if (TextUtils.isEmpty(uri)) {
            engine.cancelDisplayTaskFor(imageAware);
            listener.onLoadingStarted(uri, imageAware.getWrapperdView());
            if (options.shouldShowImageForEmptyUri()) {
                imageAware.setImageDrawable(options.getImageForEmptyUri(mConfiguration.resources));
            } else {
                imageAware.setImageDrawable(null);
            }
            listener.onLoadingFinished(uri, imageAware.getWrapperdView(), null);
            return;
        }

        if (targetSize == null) {
            targetSize = ImageSizeUtils.defineTargetSizeForView(imageAware, mConfiguration.getMaxImageSize());
        }

        String memoryCacheKey = MemoryCacheUtils.generateKey(uri, targetSize);
        listener.onLoadingStarted(uri, imageAware.getWrapperdView());
        Bitmap bmp = mConfiguration.memoryCache.get(memoryCacheKey);

        if (bmp != null && !bmp.isRecycled()) {
            if (options.shouldPostProcess()) {
                // ImageLoadingInfo imageLoadingInfo =
            }else{
                options.getDisplayer().display(bmp , imageAware , LoadedFrom.MEMORY_CACHE);
                listener.onLoadingFinished(uri , imageAware.getWrapperdView() , bmp);
            }
        } else {
            if (options.shouldShowImageOnLoading()) {
                imageAware.setImageDrawable(options.getImageOnLoading(mConfiguration.resources));
            } else if (options.isResetViewBeforeLoading()) {
                imageAware.setImageDrawable(null);
            }

            ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri, imageAware, targetSize, memoryCacheKey,
                    options, listener, progressListener, engine.getLockFromUri(uri));

            LoadAndDisplayImageTask displayImageTask = new LoadAndDisplayImageTask(engine , imageLoadingInfo , defineHandler(options));
            if (options.isSyncLoading()){
                displayImageTask.run();
            }else{
                engine.submit(displayImageTask);
            }
        }
    }


    private Handler defineHandler(DisplayImageOptions options){

        Handler handler = options.getHandler();
        if (options.isSyncLoading()){
            handler = null;
        }else{
            if (handler == null && Looper.myLooper() == Looper.getMainLooper()){
                handler = new Handler();
            }
        }



    }




}
