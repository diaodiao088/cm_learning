package core;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import core.DefaultConfigurationFactory;
import core.ImageLoaderConfiguration;
import core.imageaware.ImageAware;

/**
 * Created by zhangdan on 2017/9/30.
 *
 * comments:
 */

public class ImageLoaderEngine {

    public ImageLoaderConfiguration configuration;
    private Executor taskExecutor;
    private Executor taskExecutorForCacheImage;
    private Executor taskDistributor;

    private final AtomicBoolean paused = new AtomicBoolean();

    private final Map<Integer, String> cacheKeysForImageAwares = Collections
            .synchronizedMap(new HashMap<Integer, String>());

    private final Map<String , ReentrantLock> uriLocks = new WeakHashMap<>();

    private Object pausedLock = new Object();

    public ImageLoaderEngine(ImageLoaderConfiguration configuration){
        this.configuration = configuration;

        taskExecutor = configuration.taskExecutor;
        taskExecutorForCacheImage = configuration.taskExecutorForCachedImages;
        taskDistributor = DefaultConfigurationFactory.createTaskDistributor();
    }

    public void submit(final LoadAndDisplayImageTask task){
        taskDistributor.execute(new Runnable() {
            @Override
            public void run() {
                File image = configuration.diskCache.get(task.getLoadingUri());
                boolean isImageCacheOnDisk = image != null&& image.exists();
                initExecutorsIfNeed();
                if (isImageCacheOnDisk){
                    taskExecutorForCacheImage.execute(task);
                }else{
                    taskExecutor.execute(task);
                }
            }
        });
    }

    private void initExecutorsIfNeed(){
        if (!configuration.customExecutor && ((ExecutorService)taskExecutor).isShutdown()){
            taskExecutor = createTaskExecutor();
        }

        if (!configuration.customExecutorForCachedImages && ((ExecutorService) taskExecutorForCacheImage)
                .isShutdown()){
            taskExecutorForCacheImage = createTaskExecutor();
        }
    }

    private Executor createTaskExecutor() {
        return DefaultConfigurationFactory.createExecutor(configuration.threadPoolSize , configuration.threadPriority ,configuration.tasksProcessingType);
    }

    void prepareDisplayTaskFor(ImageAware imageAware, String memoryCacheKey) {
        cacheKeysForImageAwares.put(imageAware.getId(), memoryCacheKey);
    }

    void cancelDisplayTaskFor(ImageAware imageAware){
        cacheKeysForImageAwares.remove(imageAware.getId());
    }

    ReentrantLock getLockFromUri(String uri){

        ReentrantLock lock = uriLocks.get(uri);

        if (lock == null){
            lock = new ReentrantLock();
            uriLocks.put(uri , lock);
        }
        return lock;
    }

    public void pause(){
        paused.set(true);
    }

    public AtomicBoolean getPaused(){
        return paused;
    }

    public Object getPausedLock(){
        return pausedLock;
    }





}
